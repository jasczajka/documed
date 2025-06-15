package com.documed.backend.notifications;

import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NotificationDAO {

  private final JdbcTemplate jdbcTemplate;

  public int createNotification(
      Integer visitId,
      Integer additionalServiceId,
      NotificationStatus status,
      NotificationType type) {
    String sql =
        "INSERT INTO notification (visit_id, additional_service_id, status, type) "
            + "VALUES (?, ?, ?, ?) RETURNING id";

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setObject(1, visitId);
          ps.setObject(2, additionalServiceId);
          ps.setString(3, status.name());
          ps.setString(4, type.name());
          return ps;
        },
        keyHolder);

    return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
  }

  public void updateNotificationStatus(int notificationId, NotificationStatus status) {
    String sql = "UPDATE notification SET status = ? WHERE id = ?";
    jdbcTemplate.update(sql, status.name(), notificationId);
  }
}
