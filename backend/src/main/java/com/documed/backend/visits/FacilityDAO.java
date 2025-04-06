package com.documed.backend.visits;

import com.documed.backend.ReadDAO;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FacilityDAO implements ReadDAO<Facility> {

  private final JdbcTemplate jdbcTemplate;

  public FacilityDAO(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public Optional<Facility> getById(int id) {
    String sql = "SELECT * FROM facility WHERE id = ?";

    List<Facility> facilities =
        jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Facility(id, rs.getString("address"), rs.getString("city")),
            id);
    return Optional.ofNullable(facilities.stream().findFirst().orElse(null));
  }

  @Override
  public List<Facility> getAll() {
    String sql = "SELECT * FROM facility";
    return jdbcTemplate.query(
        sql,
        (rs, rowNum) -> {
          int id = rs.getInt("id");
          String address = rs.getString("address");
          String city = rs.getString("city");
          return new Facility(id, address, city);
        });
  }
}
