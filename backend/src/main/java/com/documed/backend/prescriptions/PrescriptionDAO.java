package com.documed.backend.prescriptions;

import com.documed.backend.FullDAO;
import com.documed.backend.medicines.Medicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class PrescriptionDAO implements FullDAO<Prescription> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PrescriptionDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Prescription create(Prescription obj) {
        return null;
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
                        (rs, rowNum) -> new Prescription(
                                id,
                                rs.getInt("access_code"),
                                rs.getString("description"),
                                rs.getDate("date"),
                                rs.getDate("expiration_date"),
                                rs.getString("pesel"),
                                rs.getString("passport_number")
                                ),
                        id);

        return Optional.ofNullable(prescriptions.stream().findFirst().orElse(null));
    }

    @Override
    public List<Prescription> getAll() {
        String sql = "SELECT * FROM prescription";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    int access_code = rs.getInt("access_code");
                    String description = rs.getString("description");
                    Date date = rs.getDate("date");
                    Date expirationDate = rs.getDate("expiration_date");
                    String pesel = rs.getString("pesel");
                    String passportNumber = rs.getString("passport_number");
                    return new Prescription(id, access_code, description, date, expirationDate, pesel, passportNumber);
                });
    }
}
