package com.documed.backend.visits;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import com.documed.backend.visits.model.VisitWithDetails;
import com.documed.backend.visits.model.VisitWithDetailsRowMapper;
import java.math.BigDecimal;
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

  private static final RowMapper<Visit> rowMapper =
      (rs, rowNum) -> {
        Visit.VisitBuilder builder =
            Visit.builder()
                .id(rs.getInt("id"))
                .status(VisitStatus.valueOf(rs.getString("status")))
                .facilityId(rs.getInt("facility_id"))
                .serviceId(rs.getInt("service_id"))
                .patientId(rs.getInt("patient_id"))
                .doctorId(rs.getInt("doctor_id"));

        // nullable
        String interview = rs.getString("interview");
        if (interview != null) builder.interview(interview);

        String diagnosis = rs.getString("diagnosis");
        if (diagnosis != null) builder.diagnosis(diagnosis);

        String recommendations = rs.getString("recommendations");
        if (recommendations != null) builder.recommendations(recommendations);

        BigDecimal totalCost = rs.getBigDecimal("total_cost");
        if (totalCost != null) builder.totalCost(totalCost);

        String patientInfo = rs.getString("patient_information");
        if (patientInfo != null) builder.patientInformation(patientInfo);

        return builder.build();
      };

  @Override
  public Visit create(Visit creationObject) {
    String sql =
        "INSERT INTO visit (status, facility_id, service_id, patient_id, doctor_id, patient_information, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, creationObject.getStatus().name());
          ps.setInt(2, creationObject.getFacilityId());
          ps.setInt(3, creationObject.getServiceId());
          ps.setInt(4, creationObject.getPatientId());
          ps.setInt(5, creationObject.getDoctorId());
          ps.setString(6, creationObject.getPatientInformation());
          ps.setBigDecimal(7, creationObject.getTotalCost());
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
    String sql = "DELETE FROM visit WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public Optional<Visit> getById(int id) {
    String sql =
        "SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id FROM visit WHERE id = ?";

    List<Visit> visits = jdbcTemplate.query(sql, rowMapper, id);

    return visits.stream().findFirst();
  }

  @Override
  public List<Visit> getAll() {
    String sql =
        """
          SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id
          FROM visit
         """;
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<VisitWithDetails> findAllWithDetails() {
    String sql =
        VISIT_DETAILS_BASE_QUERY + " ORDER BY v.id DESC, first_ts.date, first_ts.start_time";
    return jdbcTemplate.query(sql, new VisitWithDetailsRowMapper());
  }

  public Optional<VisitWithDetails> findByIdWithDetails(int id) {
    String sql =
        VISIT_DETAILS_BASE_QUERY
            + " WHERE v.id = ? ORDER BY v.id, first_ts.date, first_ts.start_time";
    List<VisitWithDetails> visits = jdbcTemplate.query(sql, new VisitWithDetailsRowMapper(), id);
    return visits.stream().findFirst();
  }

  public List<VisitWithDetails> findByPatientIdWithDetails(int patientId) {
    String sql =
        VISIT_DETAILS_BASE_QUERY
            + " WHERE v.patient_id = ? ORDER BY v.id, first_ts.date, first_ts.start_time";
    return jdbcTemplate.query(sql, new VisitWithDetailsRowMapper(), patientId);
  }

  public List<VisitWithDetails> findByDoctorIdWithDetails(int doctorId) {
    String sql =
        VISIT_DETAILS_BASE_QUERY
            + " WHERE v.doctor_id = ? ORDER BY v.id, first_ts.date, first_ts.start_time";
    return jdbcTemplate.query(sql, new VisitWithDetailsRowMapper(), doctorId);
  }

  public List<VisitWithDetails> findByPatientIdAndFacilityIdWithDetails(
      int patientId, int facilityId) {
    String sql =
        VISIT_DETAILS_BASE_QUERY
            + " WHERE v.patient_id = ? AND v.facility_id = ? ORDER BY v.id, first_ts.date, first_ts.start_time";
    return jdbcTemplate.query(sql, new VisitWithDetailsRowMapper(), patientId, facilityId);
  }

  public List<VisitWithDetails> findByDoctorIdAndFacilityIdWithDetails(
      int doctorId, int facilityId) {
    String sql =
        VISIT_DETAILS_BASE_QUERY
            + " WHERE v.doctor_id = ? AND v.facility_id = ? ORDER BY v.id, first_ts.date, first_ts.start_time";
    return jdbcTemplate.query(sql, new VisitWithDetailsRowMapper(), doctorId, facilityId);
  }

  public boolean updateVisitStatus(int visitId, VisitStatus status) {
    String sql = "UPDATE visit SET status = ? WHERE id = ?";
    int affectedRows = jdbcTemplate.update(sql, status.name(), visitId);
    return affectedRows == 1;
  }

  public List<Visit> getVisitsByPatientIdAndFacilityId(int patientId, int facilityId) {
    String sql =
        """
                  SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id
                  FROM visit
                  WHERE patient_id = ?
                  AND facility_id = ?
                 """;

    return jdbcTemplate.query(sql, rowMapper, patientId, facilityId);
  }

  public List<Visit> getVisitsByDoctorIdAndFacilityId(int doctorId, int facilityId) {
    String sql =
        """
                  SELECT DISTINCT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, doctor_id
                  FROM visit
                  WHERE doctor_id = ?
                  AND facility_id = ?
                 """;

    return jdbcTemplate.query(sql, rowMapper, doctorId, facilityId);
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

  public int getVisitPatientId(int visitId) {
    String sql = "SELECT patient_id FROM visit WHERE id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("patient_id"), visitId).stream()
        .findFirst()
        .orElseThrow();
  }

  private static final String VISIT_DETAILS_BASE_QUERY =
      """
           SELECT
               v.*,
               p.first_name AS patient_first_name,
               p.last_name AS patient_last_name,
               p.birthdate AS patient_birth_date,
               p.pesel AS patient_pesel,
               d.first_name AS doctor_first_name,
               d.last_name AS doctor_last_name,
               s.id AS service_id,
               s.name AS service_name,
               first_ts.start_time AS timeslot_start,
               last_ts.end_time AS timeslot_end,
               first_ts.date AS timeslot_date
           FROM visit v
           JOIN "User" p ON v.patient_id = p.id
           JOIN "User" d ON v.doctor_id = d.id
           JOIN service s ON v.service_id = s.id
           LEFT JOIN LATERAL (
               SELECT ts.start_time, ts.date
               FROM time_slot ts
               WHERE ts.visit_id = v.id
               ORDER BY ts.start_time ASC
               LIMIT 1
           ) first_ts ON true
           LEFT JOIN LATERAL (
               SELECT ts.end_time
               FROM time_slot ts
               WHERE ts.visit_id = v.id
               ORDER BY ts.start_time DESC
               LIMIT 1
           ) last_ts ON true
           """;
}
