package com.documed.backend.medicines;

import com.documed.backend.ReadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicineDAO implements ReadDAO<Medicine> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicineDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Medicine> getById(int id) {
        String sql = "SELECT * FROM medicine WHERE id = ?";

        List<Medicine> medicines =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> new Medicine(id,
                                rs.getString("name"),
                                rs.getString("common_name"),
                                rs.getString("packaging")),
                        id);
        return Optional.ofNullable(medicines.stream().findFirst().orElse(null));
    }

    @Override
    public List<Medicine> getAll() {
        String sql = "SELECT * FROM medicine";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String commonName = rs.getString("common_name");
                    String packaging = rs.getString("packaging");
                    return new Medicine(id, name, commonName, packaging);
                });
    }
}
