package com.documed.backend.additionalservices;

import com.documed.backend.FullDAO;
import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.ServiceDAO;
import com.documed.backend.users.UserDAO;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalServiceDAO implements FullDAO<AdditionalService, AdditionalService> {

  private final JdbcTemplate jdbcTemplate;
  private final UserDAO userDAO;
  private final ServiceDAO serviceDAO;
  private final RowMapper<AdditionalService> rowMapper;

  public AdditionalServiceDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO, ServiceDAO serviceDAO) {
    this.jdbcTemplate = jdbcTemplate;
    this.userDAO = userDAO;
    this.serviceDAO = serviceDAO;
    this.rowMapper = createRowMapper();
  }

  private RowMapper<AdditionalService> createRowMapper() {
    return (rs, rowNum) ->
        AdditionalService.builder()
            .id(rs.getInt("id"))
            .description(rs.getString("description"))
            .date(new java.util.Date(rs.getDate("date").getTime()))
            .fulfiller(
                userDAO
                    .getById(rs.getInt("fulfiller_id"))
                    .orElseThrow(() -> new NotFoundException("Fulfiller not found")))
            .patient(
                userDAO
                    .getById(rs.getInt("patient_id"))
                    .orElseThrow(() -> new NotFoundException("Patient not found")))
            .service(
                serviceDAO
                    .getById(rs.getInt("service_id"))
                    .orElseThrow(() -> new NotFoundException("Service not found")))
            .build();
  }

  @Override
  public AdditionalService create(AdditionalService additionalService) {
    String sql =
        "INSERT INTO Additional_service (description, date, fulfiller_id, patient_id, service_id) "
            + "VALUES (?, ?, ?, ?, ?) RETURNING id";

    Integer generatedId =
        jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> rs.getInt("id"),
            additionalService.getDescription(),
            new Date(additionalService.getDate().getTime()),
            additionalService.getFulfiller().getId(),
            additionalService.getPatient().getId(),
            additionalService.getService().getId());

    return getById(generatedId)
        .orElseThrow(
            () -> new IllegalStateException("Failed to retrieve created additional service"));
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM Additional_service WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public Optional<AdditionalService> getById(int id) {
    String sql = "SELECT * FROM Additional_service WHERE id = ?";
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  public List<AdditionalService> getAll() {
    String sql = "SELECT * FROM Additional_service";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<AdditionalService> getByFulfillerId(int fulfillerId) {
    String sql = "SELECT * FROM Additional_service WHERE fulfiller_id = ?";
    return jdbcTemplate.query(sql, rowMapper, fulfillerId);
  }

  public List<AdditionalService> getByPatientId(int patientId) {
    String sql = "SELECT * FROM Additional_service WHERE patient_id = ?";
    return jdbcTemplate.query(sql, rowMapper, patientId);
  }

  public List<AdditionalService> getByServiceId(int serviceId) {
    String sql = "SELECT * FROM Additional_service WHERE service_id = ?";
    return jdbcTemplate.query(sql, rowMapper, serviceId);
  }

  public int updateDescription(int id, String description) {
    String sql = "UPDATE Additional_service SET description = ? WHERE id = ?";
    return jdbcTemplate.update(sql, description, id);
  }
}
