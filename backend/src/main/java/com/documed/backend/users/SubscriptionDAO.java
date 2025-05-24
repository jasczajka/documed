package com.documed.backend.users;

import com.documed.backend.ReadDAO;
import com.documed.backend.users.model.Subscription;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SubscriptionDAO implements ReadDAO<Subscription> {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Subscription> getById(int id) {
    String sql = "SELECT id, name, price FROM subscription WHERE id = ?";

    List<Subscription> subscriptions =
        jdbcTemplate.query(
            sql,
            (rs, rowNum) ->
                Subscription.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .price(rs.getBigDecimal("price"))
                    .build());

    return subscriptions.stream().findFirst();
  }

  @Override
  public List<Subscription> getAll() {
    String sql = "SELECT id, name, price FROM subscription";

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            Subscription.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getBigDecimal("price"))
                .build());
  }
}
