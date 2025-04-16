package com.documed.backend.users;

import com.documed.backend.ReadDAO;
import com.documed.backend.users.model.Specialization;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
            (rs, rowNum) -> Specialization.builder().id(id).name(rs.getString("name")).build(),
            id);

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

          return Specialization.builder().id(id).name(name).build();
        });
  }
}
