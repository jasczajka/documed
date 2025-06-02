package com.documed.backend.users;

import com.documed.backend.users.model.SubscriptionToService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SubscriptionToServiceDAO {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<SubscriptionToService> rowMapper =
      (rs, rowNum) ->
          new SubscriptionToService(
              rs.getInt("subscription_id"), rs.getInt("service_id"), rs.getInt("discount"));

  public List<SubscriptionToService> getAll() {
    String sql = "SELECT service_id, subscription_id, discount FROM subscription_service";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<SubscriptionToService> getForSubscription(int id) {
    String sql =
        "SELECT service_id, subscription_id, discount FROM subscription_service WHERE subscription_id = ?";
    return jdbcTemplate.query(sql, rowMapper, id);
  }

  public int getDiscountForService(int serviceId, int subscriptionId) {
    String sql =
        "SELECT discount FROM subscription_service WHERE service_id = ? AND subscription_id = ?";

    List<Integer> results =
        jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("discount"), serviceId, subscriptionId);

    return results.isEmpty() ? 0 : results.get(0);
  }

  public int create(SubscriptionToService subscriptionToService) {
    String sql =
        "INSERT INTO subscription_service (service_id, subscription_id, discount) VALUES (?, ?, ?)";
    return jdbcTemplate.update(
        sql,
        subscriptionToService.getServiceId(),
        subscriptionToService.getSubscriptionId(),
        subscriptionToService.getDiscount());
  }

  public int update(SubscriptionToService subscriptionToService) {
    String sql =
        "UPDATE subscription_service SET discount = ? WHERE service_id = ? AND subscription_id = ?";
    return jdbcTemplate.update(
        sql,
        subscriptionToService.getDiscount(),
        subscriptionToService.getServiceId(),
        subscriptionToService.getSubscriptionId());
  }

  public void deleteForService(int serviceId) {
    String sql = "DELETE FROM subscription_service WHERE service_id = ?";
    jdbcTemplate.update(sql, serviceId);
  }

  public void deleteForSubscription(int subscriptionId) {
    String sql = "DELETE FROM subscription_service WHERE subscription_id = ?";
    jdbcTemplate.update(sql, subscriptionId);
  }
}
