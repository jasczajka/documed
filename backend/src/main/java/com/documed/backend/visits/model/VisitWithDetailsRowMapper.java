package com.documed.backend.visits.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;

public class VisitWithDetailsRowMapper implements RowMapper<VisitWithDetails> {

  @Override
  public @NonNull VisitWithDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
    return VisitWithDetails.builder()
        .id(rs.getInt("id"))
        .status(VisitStatus.valueOf(rs.getString("status")))
        .interview(rs.getString("interview"))
        .diagnosis(rs.getString("diagnosis"))
        .recommendations(rs.getString("recommendations"))
        .totalCost(rs.getBigDecimal("total_cost"))
        .facilityId(rs.getInt("facility_id"))
        .serviceId(rs.getInt("service_id"))
        .serviceName(rs.getString("service_name"))
        .patientInformation(rs.getString("patient_information"))
        .patientId(rs.getInt("patient_id"))
        .patientFullName(
            rs.getString("patient_first_name") + " " + rs.getString("patient_last_name"))
        .patientBirthDate(getLocalDateOrNull(rs, "patient_birth_date"))
        .patientPesel(rs.getString("patient_pesel"))
        .doctorId(rs.getInt("doctor_id"))
        .doctorFullName(rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"))
        .startTime(getLocalTimeOrNull(rs, "timeslot_start"))
        .endTime(getLocalTimeOrNull(rs, "timeslot_end"))
        .date(getLocalDateOrNull(rs, "timeslot_date"))
        .build();
  }

  private LocalTime getLocalTimeOrNull(ResultSet rs, String column) throws SQLException {
    Time t = rs.getTime(column);
    return t != null ? t.toLocalTime() : null;
  }

  private LocalDate getLocalDateOrNull(ResultSet rs, String column) throws SQLException {
    Date d = rs.getDate(column);
    return d != null ? d.toLocalDate() : null;
  }
}
