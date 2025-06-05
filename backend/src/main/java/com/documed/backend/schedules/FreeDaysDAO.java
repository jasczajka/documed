package com.documed.backend.schedules;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.schedules.model.FreeDays;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FreeDaysDAO implements FullDAO<FreeDays, FreeDays> {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public FreeDays create(FreeDays creationObject) {
    String sql =
        "INSERT INTO free_days (user_id, start_date, end_date) VALUES (?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setLong(1, creationObject.getUserId());
          ps.setDate(2, Date.valueOf(creationObject.getStartDate()));
          ps.setDate(3, Date.valueOf(creationObject.getEndDate()));
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      creationObject.setId(key.intValue());
      return creationObject;
    } else {
      throw new CreationFailException("Failed retrieve id value");
    }
  }

  @Override
  public int delete(int id) {
    return 0;
  }

  @Override
  public Optional<FreeDays> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<FreeDays> getAll() {
    return List.of();
  }
}
