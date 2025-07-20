package com.documed.backend.visits;

import com.documed.backend.ReadDAO;
import com.documed.backend.visits.model.Facility;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
            (rs, rowNum) ->
                Facility.builder()
                    .id(id)
                    .address(rs.getString("address"))
                    .city(rs.getString("city"))
                    .build(),
            id);
    return facilities.stream().findFirst();
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
          return Facility.builder().id(id).address(address).city(city).build();
        });
  }

  public Facility create(Facility facility) {
    String sql = "INSERT INTO facility (address, city) VALUES (?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        con -> {
          PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, facility.getAddress());
          ps.setString(2, facility.getCity());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      facility.setId(key.intValue());
    } else {
      throw new RuntimeException("Failed retrieve id value");
    }

    return facility;
  }
}
