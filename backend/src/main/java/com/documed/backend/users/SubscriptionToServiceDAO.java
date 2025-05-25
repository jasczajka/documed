package com.documed.backend.users;

import com.documed.backend.users.model.SubscriptionToService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SubscriptionToServiceDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SubscriptionToService> rowMapper =
            (rs, rowNum) -> new SubscriptionToService(
                    rs.getInt("service_id"),
                    rs.getInt("subscription_id"),
                    rs.getInt("discount")
            );

    public List<SubscriptionToService> getForSubscription(int id) {
        String sql = "SELECT service_id, subscription_id, discount FROM subscription_service WHERE subscription_id = ?";
        return jdbcTemplate.query(sql, rowMapper, id);
    }

    public int getDiscountForService(int serviceId, int subscriptionId) {
        String sql = "SELECT discount FROM subscription_service WHERE service_id = ? AND subscription_id = ?";
        Integer discount = jdbcTemplate.queryForObject(sql, Integer.class, serviceId, subscriptionId);

        if (discount == null) {
            return 0;
        } else {
            return discount;
        }
    }

    public int create(SubscriptionToService subscriptionToService) {
        String sql = "INSERT INTO subscription_service (service_id, subscription_id, discount) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, subscriptionToService.getServiceId(), subscriptionToService.getSubscriptionId(), subscriptionToService.getDiscount());
    }

    public int update(SubscriptionToService subscriptionToService) {
        String sql = "UPDATE subscription_service SET discount = ? WHERE service_id = ? AND subscription_id = ?";
        return jdbcTemplate.update(sql, subscriptionToService.getDiscount(), subscriptionToService.getServiceId(), subscriptionToService.getSubscriptionId());
    }

}
