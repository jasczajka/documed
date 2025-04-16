package com.documed.backend.prescriptions;

import com.documed.backend.FullDAO;
import com.documed.backend.medicines.MedicineService;
import com.documed.backend.medicines.model.Medicine;
import java.sql.PreparedStatement;
import java.sql.Statement;
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
public class PrescriptionDAO implements FullDAO<Prescription, Integer> {

  private final JdbcTemplate jdbcTemplate;
  private final MedicineService medicineService;

  private final RowMapper<Prescription> rowMapper =
      (rs, rowNum) ->
          Prescription.builder()
              .id(rs.getInt("id"))
              .accessCode(rs.getInt("access_code"))
              .date(rs.getDate("date"))
              .expirationDate(rs.getDate("expiration_date"))
              .status(PrescriptionStatus.valueOf(rs.getString("status")))
              .build();

  @Override
  public Prescription create(Integer visitId) {
    String sql = "INSERT INTO prescription (visit_id) VALUES (?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setInt(1, visitId);
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      return Prescription.builder().id(key.intValue()).build();
    } else {
      throw new IllegalStateException("Failed retrieve id value");
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
    String sql = "SELECT * FROM prescription WHERE id = ?";

    List<Prescription> prescriptions = jdbcTemplate.query(sql, rowMapper, id);

    return prescriptions.stream().findFirst();
  }

  public List<Prescription> getAll() {
    throw new UnsupportedOperationException();
  }

  public Prescription getPrescriptionForVisit(int visitId) {
    String sql = "SELECT * FROM prescription WHERE visit_id = ?";
    return jdbcTemplate.query(sql, rowMapper, visitId).getFirst();
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
                        SELECT *
                        FROM prescription
                        JOIN visit ON prescription.visit_id = visit.id
                        WHERE patient_id = ?
             """;
    return jdbcTemplate.query(sql, rowMapper, userId);
  }

  public Prescription issuePrescription(int prescriptionId) {
    String sql =
        """
                UPDATE prescription
                SET date = current_date, status = 'ISSUED', expiration_date = current_date
                WHERE id = ?
                """;

    int rowsAffected = jdbcTemplate.update(sql, prescriptionId);

    if (rowsAffected > 0) {
      return getById(prescriptionId)
          .orElseThrow(() -> new IllegalStateException("Could not found prescription"));
    } else {
      throw new IllegalStateException("Failed update prescription");
    }
  }
}
