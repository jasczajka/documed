package com.documed.backend.visits;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.visits.model.Feedback;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackDAO implements FullDAO<Feedback, Feedback> {

  private final JdbcTemplate jdbcTemplate;

  public FeedbackDAO(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static final RowMapper<Feedback> feedbackRowMapper =
      (rs, rowNum) ->
          Feedback.builder()
              .id(rs.getInt("id"))
              .rating(rs.getInt("rating"))
              .text(rs.getString("text"))
              .visitId(rs.getInt("visit_id"))
              .build();

  public Optional<Feedback> getById(int id) {
    String sql = "SELECT * FROM feedback WHERE id = ?";
    List<Feedback> feedbackList = jdbcTemplate.query(sql, feedbackRowMapper, id);
    return feedbackList.stream().findFirst();
  }

  @Override
  public List<Feedback> getAll() {
    String sql = "SELECT * FROM feedback";
    return jdbcTemplate.query(sql, feedbackRowMapper);
  }

  @Override
  public Feedback create(Feedback feedback) {
    String sql = "INSERT INTO feedback (rating, text, visit_id) VALUES (?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setInt(1, feedback.getRating());
          ps.setString(2, feedback.getText());
          ps.setInt(3, feedback.getVisitId());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();

    if (key != null) {
      feedback.setId(key.intValue());
      return feedback;
    } else {
      throw new CreationFailException("Failed to create feedback");
    }
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM feedback WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  public Optional<Feedback> getByVisitId(int visitId) {
    String sql = "SELECT * FROM feedback WHERE visit_id = ?";

    List<Feedback> feedbackList =
        jdbcTemplate.query(
            sql,
            (rs, rowNum) ->
                Feedback.builder()
                    .id(rs.getInt("id"))
                    .rating(rs.getInt("rating"))
                    .text(rs.getString("text"))
                    .visitId(rs.getInt("visit_id"))
                    .build(),
            visitId);
    return feedbackList.stream().findFirst();
  }
}
