package com.documed.backend.schedules;

import com.documed.backend.FullDAO;
import com.documed.backend.schedules.model.TimeSlot;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TimeSlotDAO implements FullDAO<TimeSlot, TimeSlot> {

  private final JdbcTemplate jdbcTemplate;

  public TimeSlotDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<TimeSlot> rowMapper =
      (rs, rowNum) ->
          TimeSlot.builder()
              .id(rs.getInt("id"))
              .visitId(rs.getInt("visit_id"))
              .doctorId(rs.getInt("doctor_id"))
              .startTime(rs.getTime("start_time").toLocalTime())
              .endTime(rs.getTime("end_time").toLocalTime())
              .date((rs.getDate("date").toLocalDate()))
              .isBusy(rs.getBoolean("is_busy"))
              .build();

  public TimeSlot create(TimeSlot timeSlot) {
    String sql =
        "INSERT INTO time_slot (doctor_id, start_time, end_time, date, is_busy) VALUES (?, ?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setInt(1, timeSlot.getDoctorId());
          ps.setTime(2, Time.valueOf(timeSlot.getStartTime()));
          ps.setTime(3, Time.valueOf(timeSlot.getEndTime()));
          ps.setDate(4, Date.valueOf(timeSlot.getDate()));
          ps.setBoolean(5, timeSlot.isBusy());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      timeSlot.setId(key.intValue());
      return timeSlot;
    } else {
      throw new RuntimeException("Failed retrieve id value");
    }
  }

  @Override
  public int delete(int id) {
    return 0;
  }

  public Optional<TimeSlot> getById(int id) {
    String sql = "SELECT * FROM time_slot WHERE id = ?";
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  @Override
  public List<TimeSlot> getAll() {
    return List.of();
  }

  public List<TimeSlot> getAvailableTimeSlotsByDoctorAndDate(int doctorId, LocalDate date) {
    String sql =
        """
        SELECT id, doctor_id, start_time, end_time, date, is_busy, visit_id
        FROM time_slot
        WHERE doctor_id = ? AND date = ? AND is_busy = false
""";

    return jdbcTemplate.query(sql, rowMapper, doctorId, Date.valueOf(date));
  }

  public TimeSlot update(TimeSlot timeSlot) {
    String sql =
        """
                UPDATE time_slot
                SET doctor_id = ?, start_time = ?, end_time = ?, date = ?, is_busy = ?, visit_id = ?
                WHERE id = ?
                """;

    int affectedRows =
        jdbcTemplate.update(
            sql,
            timeSlot.getDoctorId(),
            Time.valueOf(timeSlot.getStartTime()),
            Time.valueOf(timeSlot.getEndTime()),
            Date.valueOf(timeSlot.getDate()),
            timeSlot.isBusy(),
            timeSlot.getVisitId(),
            timeSlot.getId());

    if (affectedRows == 1) {
      return timeSlot;
    } else {
      throw new RuntimeException("Failed to update time slot");
    }
  }
}
