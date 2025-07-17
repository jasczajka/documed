package com.documed.backend.additionalservices;

import com.documed.backend.FullDAO;
import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.additionalservices.model.AdditionalServiceWithDetails;
import com.documed.backend.additionalservices.model.AdditionalServiceWithDetailsRowMapper;
import com.documed.backend.exceptions.CreationFailException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalServiceDAO implements FullDAO<AdditionalService, AdditionalService> {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<AdditionalService> rowMapper;

  public AdditionalServiceDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.rowMapper = createRowMapper();
  }

  private RowMapper<AdditionalService> createRowMapper() {
    return (rs, rowNum) ->
        AdditionalService.builder()
            .id(rs.getInt("id"))
            .description(rs.getString("description"))
            .date(rs.getDate("date").toLocalDate())
            .fulfillerId(rs.getInt("fulfiller_id"))
            .patientId(rs.getInt("patient_id"))
            .serviceId(rs.getInt("service_id"))
            .totalCost(rs.getBigDecimal("total_cost"))
            .build();
  }

  @Override
  public AdditionalService create(AdditionalService additionalService) {
    String sql =
        "INSERT INTO Additional_service (description, date, fulfiller_id, patient_id, service_id, total_cost) "
            + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

    Integer generatedId =
        jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> rs.getInt("id"),
            additionalService.getDescription(),
            Date.valueOf(additionalService.getDate()),
            additionalService.getFulfillerId(),
            additionalService.getPatientId(),
            additionalService.getServiceId(),
            additionalService.getTotalCost());

    if (generatedId == null) {
      throw new CreationFailException("Failed to retrieve ID after insert.");
    }

    return getById(generatedId)
        .orElseThrow(
            () -> new CreationFailException("Failed to retrieve created additional service"));
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM Additional_service WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  public List<AdditionalServiceWithDetails> findAllWithDetailsBetweenDates(LocalDate startDate) {
    String sql =
        ADDITIONAL_SERVICE_DETAILS_BASE_QUERY + " WHERE a.date >= ? " + " ORDER BY a.id DESC";
    return jdbcTemplate.query(sql, new AdditionalServiceWithDetailsRowMapper(), startDate);
  }

  public Optional<AdditionalServiceWithDetails> findByIdWithDetails(int id) {
    String sql = ADDITIONAL_SERVICE_DETAILS_BASE_QUERY + " WHERE a.id = ?";
    List<AdditionalServiceWithDetails> services =
        jdbcTemplate.query(sql, new AdditionalServiceWithDetailsRowMapper(), id);
    return services.stream().findFirst();
  }

  public List<AdditionalServiceWithDetails> findByPatientIdWithDetailsBetweenDates(
      int patientId, LocalDate startDate) {
    String sql =
        ADDITIONAL_SERVICE_DETAILS_BASE_QUERY
            + " WHERE a.patient_id = ? AND a.date >= ?"
            + " ORDER BY a.date DESC, a.id DESC";
    return jdbcTemplate.query(
        sql, new AdditionalServiceWithDetailsRowMapper(), patientId, startDate);
  }

  public List<AdditionalServiceWithDetails> findByFulfillerIdWithDetailsBetweenDates(
      int fulfillerId, LocalDate startDate) {
    String sql =
        ADDITIONAL_SERVICE_DETAILS_BASE_QUERY
            + " WHERE a.fulfiller_id = ? AND a.date >= ?"
            + " ORDER BY a.date DESC, a.id DESC";
    return jdbcTemplate.query(
        sql, new AdditionalServiceWithDetailsRowMapper(), fulfillerId, startDate);
  }

  public List<AdditionalServiceWithDetails> findByServiceIdWithDetailsBetweenDates(
      int serviceId, LocalDate startDate) {
    String sql =
        ADDITIONAL_SERVICE_DETAILS_BASE_QUERY
            + " WHERE a.service_id = ? AND a.date >= ?"
            + " ORDER BY a.date DESC, a.id DESC";
    return jdbcTemplate.query(
        sql, new AdditionalServiceWithDetailsRowMapper(), serviceId, startDate);
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

  public int updateDescription(int id, String description) {
    String sql = "UPDATE Additional_service SET description = ? WHERE id = ?";
    return jdbcTemplate.update(sql, description, id);
  }

  private static final String ADDITIONAL_SERVICE_DETAILS_BASE_QUERY =
      """
           SELECT
               a.*,
               p.first_name AS patient_first_name,
               p.last_name AS patient_last_name,
               p.pesel AS patient_pesel,
                 p.passport_number as patient_passport_number,
               f.first_name AS fulfiller_first_name,
               f.last_name AS fulfiller_last_name,
               s.name AS service_name
           FROM Additional_service a
           JOIN "User" p ON a.patient_id = p.id AND p.account_status != 'DEACTIVATED'
           JOIN "User" f ON a.fulfiller_id = f.id
           JOIN service s ON a.service_id = s.id
           """;
}
