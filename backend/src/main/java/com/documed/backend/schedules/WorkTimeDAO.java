package com.documed.backend.schedules;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.schedules.model.WorkTime;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WorkTimeDAO implements FullDAO<WorkTime, WorkTime> {

  private final JdbcTemplate jdbcTemplate;

  public WorkTimeDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<WorkTime> rowMapper =
      (rs, rowNum) ->
          WorkTime.builder()
              .id(rs.getInt("id"))
              .userId(rs.getInt("user_id"))
              .dayOfWeek(DayOfWeek.of(rs.getInt("day_of_week")))
              .startTime(rs.getTime("start_time").toLocalTime())
              .endTime(rs.getTime("end_time").toLocalTime())
              .facilityId(rs.getInt("facility_id"))
              .build();

  @Override
  public WorkTime create(WorkTime creationObject) {
    String sql =
        "INSERT INTO worktime (user_id, day_of_week, start_time, end_time, facility_id) VALUES (?, ?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setInt(1, creationObject.getUserId());
          ps.setInt(2, creationObject.getDayOfWeek().getValue());
          ps.setTime(3, Time.valueOf(creationObject.getStartTime()));
          ps.setTime(4, Time.valueOf(creationObject.getEndTime()));
          ps.setInt(5, creationObject.getFacilityId());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      creationObject.setId(key.intValue());
      return creationObject;
    } else {
      throw new IllegalStateException("Failed retrieve id value");
    }
  }

  public List<WorkTime> getWorkTimesForUser(int userId) {
    String sql =
        "SELECT id, user_id, day_of_week, start_time, end_time, facility_id FROM worktime WHERE user_id = ?";

    return jdbcTemplate.query(sql, rowMapper, userId);
  }

  public WorkTime updateWorkTime(WorkTime workTime) {
    String sql =
        "INSERT INTO worktime (user_id, day_of_week, start_time, end_time, facility_id) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON CONFLICT (user_id, day_of_week) DO UPDATE "
            + "SET start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time, facility_id = EXCLUDED.facility_id";
    int affectedRows =
        jdbcTemplate.update(
            sql,
            workTime.getUserId(),
            workTime.getDayOfWeek().getValue(),
            Time.valueOf(workTime.getStartTime()),
            Time.valueOf(workTime.getEndTime()),
            workTime.getFacilityId());

    if (affectedRows == 1) {
      return getByUserIdAndDayOfWeek(workTime.getUserId(), workTime.getDayOfWeek().getValue())
          .orElseThrow(RuntimeException::new);
    } else {
      throw new BadRequestException("Failed to upsert worktime");
    }
  }

  public Optional<WorkTime> getByUserIdAndDayOfWeek(int userId, int dayOfWeek) {
    String sql =
        "SELECT id, user_id, day_of_week, start_time, end_time, facility_id FROM worktime WHERE user_id = ? AND day_of_week = ?";
    return jdbcTemplate.query(sql, rowMapper, userId, dayOfWeek).stream().findFirst();
  }

  @Override
  public int delete(int id) {
    return 0;
  }

  @Override
  public Optional<WorkTime> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<WorkTime> getAll() {
    String sql = "SELECT id, user_id, day_of_week, start_time, end_time, facility_id FROM worktime";
    return jdbcTemplate.query(sql, rowMapper);
  }
}
