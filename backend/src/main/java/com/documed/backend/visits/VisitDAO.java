package com.documed.backend.visits;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VisitDAO implements FullDAO<Visit, Visit> {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Visit> rowMapper =
      (rs, rowNum) ->
          Visit.builder()
              .id(rs.getInt("id"))
              .status(VisitStatus.valueOf(rs.getString("status")))
              .interview(rs.getString("interview"))
              .diagnosis(rs.getString("diagnosis"))
              .recommendations(rs.getString("recommendations"))
              .totalCost(rs.getBigDecimal("total_cost"))
              .facilityId(rs.getInt("facility_id"))
              .serviceId(rs.getInt("service_id"))
              .patientId(rs.getInt("patient_id"))
              .patientInformation(rs.getString("patient_information"))
              .build();

  @Override
  public Visit create(Visit creationObject) {
    String sql =
        "INSERT INTO visit (status, facility_id, service_id, patient_id, patient_information, total_cost) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, creationObject.getStatus().name());
          ps.setInt(2, creationObject.getFacilityId());
          ps.setInt(3, creationObject.getServiceId());
          ps.setInt(4, creationObject.getPatientId());
          ps.setString(5, creationObject.getPatientInformation());
          ps.setBigDecimal(6, creationObject.getTotalCost());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      creationObject.setId(key.intValue());
      return creationObject;
    } else {
      throw new CreationFailException("Failed create subscription");
    }
  }

  @Override
  public int delete(int id) {
    throw new UnsupportedOperationException("Operation nor supported.");
  }

  @Override
  public Optional<Visit> getById(int id) {
    String sql =
        "SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id FROM visit WHERE id = ?";

    List<Visit> visits = jdbcTemplate.query(sql, rowMapper, id);

    return visits.stream().findFirst();
  }

  @Override
  public List<Visit> getAll() {
    return List.of();
  }

  public boolean updateVisitStatus(int visitId, VisitStatus status) {
    String sql = "UPDATE visit SET status = ? WHERE id = ?";
    int affectedRows = jdbcTemplate.update(sql, status.name(), visitId);
    return affectedRows == 1;
  }

  public List<Visit> getVisitsByPatientId(int patientId) {
    String sql =
        "SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id "
            + "FROM visit WHERE patient_id = ?";

    return jdbcTemplate.query(sql, rowMapper, patientId);
  }

  public List<Visit> getVisitsByDoctorId(int doctorId) {
    String sql =
        "SELECT DISTINCT v.id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id "
            + "FROM visit v "
            + "JOIN time_slot ON v.id = time_slot.visit_id "
            + "WHERE time_slot.doctor_id = ?";

    return jdbcTemplate.query(sql, rowMapper, doctorId);
  }

  public Visit update(Visit visit) {
    String sql =
        """
        UPDATE visit
        SET interview = ?,
        diagnosis = ?,
        recommendations = ?
        WHERE id = ?;
  """;

    jdbcTemplate.update(
        sql, visit.getInterview(), visit.getDiagnosis(), visit.getRecommendations(), visit.getId());
    return visit;
  }

  public VisitStatus getVisitStatus(int visitId) {
    String sql = "SELECT status FROM visit WHERE id = ?";
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> VisitStatus.valueOf(rs.getString("status")), visitId)
        .stream()
        .findFirst()
        .orElseThrow();
  }
}
