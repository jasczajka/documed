package com.documed.backend.prescriptions;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.medicines.MedicineService;
import com.documed.backend.medicines.model.Medicine;
import com.documed.backend.prescriptions.model.CreatePrescriptionObject;
import com.documed.backend.prescriptions.model.Prescription;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class PrescriptionDAO implements FullDAO<Prescription, CreatePrescriptionObject> {

  private final JdbcTemplate jdbcTemplate;
  private final MedicineService medicineService;

  private final RowMapper<Prescription> rowMapper =
      (rs, rowNum) ->
          Prescription.builder()
              .id(rs.getInt("id"))
              .accessCode(rs.getInt("access_code"))
              .date(rs.getDate("date").toLocalDate())
              .expirationDate(rs.getDate("expiration_date").toLocalDate())
              .status(PrescriptionStatus.valueOf(rs.getString("status")))
              .issuingDoctorFullName(
                  rs.getString("doctor_first_name") + " " + rs.getString("doctor_last_name"))
              .build();

  @Override
  public Prescription create(CreatePrescriptionObject createObject) {
    String sql = "INSERT INTO prescription (visit_id, expiration_date) VALUES (?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setInt(1, createObject.visitId());
          ps.setObject(2, createObject.expirationDate());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      String selectSql =
          """
                SELECT p.id, p.access_code, p.date, p.expiration_date, p.status,
                  d.first_name AS doctor_first_name, d.last_name AS doctor_last_name
                FROM prescription p
                JOIN visit v ON p.visit_id = v.id
                JOIN "User" d ON v.doctor_id = d.id
                WHERE p.id = ?
                """;
      return jdbcTemplate.queryForObject(selectSql, rowMapper, key.intValue());
    } else {
      throw new IllegalStateException("Failed to retrieve generated ID for prescription");
    }
  }

  @Override
  public int delete(int id) {
    String deleteMedicinesPrescription =
        "DELETE FROM medicine_prescription WHERE prescription_id = ?";
    String deletePrescription = "DELETE FROM prescription WHERE id = ?";
    jdbcTemplate.update(deleteMedicinesPrescription, id);
    return jdbcTemplate.update(deletePrescription, id);
  }

  @Override
  public Optional<Prescription> getById(int id) {
    String sql =
        """
            SELECT p.id, p.access_code, p.date, p.expiration_date, p.status,
              d.first_name AS doctor_first_name, d.last_name AS doctor_last_name
            FROM prescription p
            JOIN visit v ON p.visit_id = v.id
            JOIN "User" d ON v.doctor_id = d.id
            WHERE p.id = ?
            """;

    List<Prescription> prescriptions = jdbcTemplate.query(sql, rowMapper, id);
    return prescriptions.stream().findFirst();
  }

  public Integer getUserIdForPrescriptionById(int id) {
    String sql =
        """
            SELECT u.id
            FROM "User" u
            JOIN visit v ON u.id = v.patient_id
            JOIN prescription p ON v.id = p.visit_id
            WHERE p.id = ?
            """;

    List<Integer> userIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id"), id);
    return userIds.stream()
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Patient not found"));
  }

  public List<Prescription> getAll() {
    String sql =
        """
            SELECT SELECT p.id, p.access_code, p.date, p.expiration_date, p.status,
              d.first_name AS doctor_first_name, d.last_name AS doctor_last_name
            FROM prescription p
            JOIN visit v ON p.visit_id = v.id
            JOIN "User" d ON v.doctor_id = d.id
            """;
    return jdbcTemplate.query(sql, rowMapper);
  }

  public Optional<Prescription> getPrescriptionForVisit(int visitId) {
    String sql =
        """
            SELECT p.id, p.access_code, p.date, p.expiration_date, p.status,
              d.first_name AS doctor_first_name, d.last_name AS doctor_last_name
            FROM prescription p
            JOIN visit v ON p.visit_id = v.id
            JOIN "User" d ON v.doctor_id = d.id
            WHERE p.visit_id = ?
            """;
    return jdbcTemplate.query(sql, rowMapper, visitId).stream().findFirst();
  }

  public Optional<Medicine> addMedicineToPrescription(
      int prescriptionId, String medicineId, int amount) {
    String sql =
        "INSERT INTO medicine_prescription (prescription_id, medicine_id, amount) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, prescriptionId, medicineId, amount);
    return medicineService.getById(medicineId);
  }

  public int removeMedicineFromPrescription(int prescriptionId, String medicineId) {
    String sql = "DELETE FROM medicine_prescription WHERE prescription_id = ? AND medicine_id = ?";
    return jdbcTemplate.update(sql, prescriptionId, medicineId);
  }

  public List<Prescription> getPrescriptionsForUser(int userId) {
    String sql =
        """
            SELECT p.*, d.first_name AS doctor_first_name, d.last_name AS doctor_last_name
            FROM prescription p
            JOIN visit v ON p.visit_id = v.id
            JOIN "User" d ON v.doctor_id = d.id
            WHERE v.patient_id = ?
            """;
    return jdbcTemplate.query(sql, rowMapper, userId);
  }

  public int updatePrescriptionStatus(int prescriptionId, PrescriptionStatus status) {
    String sql =
        """
        UPDATE prescription
        SET status = ?, date = current_date
        WHERE id = ?;
        """;
    return jdbcTemplate.update(sql, status.name(), prescriptionId);
  }

  public void updatePrescriptionExpirationDate(int prescriptionId, LocalDate newExpirationDate) {
    String sql = "UPDATE prescription SET expiration_date = ? WHERE id = ?";
    jdbcTemplate.update(sql, newExpirationDate, prescriptionId);
  }

  public int getNumberOfMedicinesOnPrescriptionByVisitId(int visitId) {
    String sql =
        """
                  SELECT COUNT(*)
                  FROM medicine_prescription mp
                  JOIN prescription p ON mp.prescription_id = p.id
                  WHERE p.visit_id = ?
                 """;
    return jdbcTemplate.queryForObject(sql, Integer.class, visitId);
  }

  public Optional<Integer> getPrescriptionIdForVisitId(int visitId) {
    String sql = "SELECT id FROM prescription WHERE visit_id = ?";
    return jdbcTemplate.query(
        sql, rs -> rs.next() ? Optional.of(rs.getInt("id")) : Optional.empty(), visitId);
  }
}
