package com.documed.backend.schedules;

import com.documed.backend.FullDAO;
import com.documed.backend.schedules.model.WorkTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public class WorkTimeDAO implements FullDAO<WorkTime, WorkTime> {

    private final JdbcTemplate jdbcTemplate;

    public WorkTimeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WorkTime create(WorkTime creationObject) {
        String sql =
                "INSERT INTO worktime (user_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?) RETURNING id";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, creationObject.getUserId());
                    ps.setInt(2, creationObject.getDayOfWeek().getValue());
                    ps.setTime(3, Time.valueOf(creationObject.getStartTime()));
                    ps.setTime(4, Time.valueOf(creationObject.getEndTime()));
                    return ps;
                },
                keyHolder);

        if (keyHolder.getKey() != null) {
            creationObject.setId(keyHolder.getKey().intValue());
        } else {
            throw new RuntimeException("Failed retrieve id value");
        }

        return null;
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
        return List.of();
    }
}
