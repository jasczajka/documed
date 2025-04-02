package com.documed.backend.users;

import com.documed.backend.ReadDAO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class SpecializationDAO implements ReadDAO<Specialization> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Specialization> getById(int id) {
        String sql = "SELECT * FROM specialization WHERE id = ?";

        List<Specialization> specializations =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> new Specialization(id, rs.getString("name")),
                        id
                );

        return specializations.stream().findFirst();
    }

    @Override
    public List<Specialization> getAll() {
        String sql = "SELECT * FROM specialization";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");

                    return new Specialization(id, name);
                });
    }

}
