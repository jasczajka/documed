package com.documed.backend.prescriptions;

import com.documed.backend.FullDAO;
import com.documed.backend.medicines.Medicine;
import com.documed.backend.services.ServiceType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class PrescriptionDAO implements FullDAO<Prescription> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Prescription create(Prescription obj) {
        String sql =
                "INSERT INTO prescription (access_code, visit_id, description, date, pesel, passport_number) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, obj.getAccessCode());
                    ps.setInt(2, obj.getVisit().getId());
                    ps.setString(3, obj.getDescription());
                    ps.setDate(4, (java.sql.Date) obj.getDate());
                    ps.setString(5, obj.getPesel());
                    ps.setString(6, obj.getPassportNumber());
                    return ps;
                },
                keyHolder);

        if (keyHolder.getKey() != null) {
            obj.setId(keyHolder.getKey().intValue());
        } else {
            throw new RuntimeException("Failed retrieve id value");
        }

        return obj;
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public Optional<Prescription> getById(int id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        List<Prescription> prescriptions =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> Prescription.builder()
                                .id(id)
                                .accessCode(rs.getInt("access_code"))
                                .description(rs.getString("description"))
                                .date(rs.getDate("date"))
                                .expirationDate(rs.getDate("expiration_date"))
                                .pesel(rs.getString("pesel"))
                                .passportNumber(rs.getString("passport_number"))
                                .status(PrescriptionStatus.valueOf(rs.getString("status")))
                                .build(),
                        id);

        return Optional.ofNullable(prescriptions.stream().findFirst().orElse(null));
    }

    public List<Prescription> getAll(){
        return null;
    }

    public List<Prescription> getAllPrescriptionsForVisit(int visitId) {
        String sql = "SELECT * FROM prescription WHERE visit_id = ?";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    int accessCode = rs.getInt("access_code");
                    String description = rs.getString("description");
                    Date date = rs.getDate("date");
                    Date expirationDate = rs.getDate("expiration_date");
                    String pesel = rs.getString("pesel");
                    String passportNumber = rs.getString("passport_number");

                    return Prescription.builder()
                            .id(id)
                            .accessCode(accessCode)
                            .description(description)
                            .date(date)
                            .expirationDate(expirationDate)
                            .pesel(pesel)
                            .passportNumber(passportNumber)
                            .build();
                });
    }

    public int removePrescriptionFromVisit(int prescriptionId) {
        jdbcTemplate.update("DELETE FROM medicine_prescription WHERE prescription_id = ?", prescriptionId);
        return jdbcTemplate.update("DELETE FROM prescription WHERE id = ?", prescriptionId);
    }


}
