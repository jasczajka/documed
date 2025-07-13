package com.documed.backend.additionalservices.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;

public class AdditionalServiceWithDetailsRowMapper
    implements RowMapper<AdditionalServiceWithDetails> {

  @Override
  public @NonNull AdditionalServiceWithDetails mapRow(ResultSet rs, int rowNum)
      throws SQLException {
    return AdditionalServiceWithDetails.builder()
        .id(rs.getInt("id"))
        .description(rs.getString("description"))
        .date(Objects.requireNonNull(getLocalDateOrNull(rs, "date")))
        .totalCost(rs.getBigDecimal("total_cost"))
        .fulfillerId(rs.getInt("fulfiller_id"))
        .fulfillerFullName(
            rs.getString("fulfiller_last_name") + " " + rs.getString("fulfiller_first_name"))
        .patientId(rs.getInt("patient_id"))
        .patientFullName(
            rs.getString("patient_last_name") + " " + rs.getString("patient_first_name"))
        .patientPesel(rs.getString("patient_pesel"))
        .serviceId(rs.getInt("service_id"))
        .serviceName(rs.getString("service_name"))
        .build();
  }

  private LocalDate getLocalDateOrNull(ResultSet rs, String column) throws SQLException {
    Date d = rs.getDate(column);
    return d != null ? d.toLocalDate() : null;
  }
}
