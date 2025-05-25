package com.documed.backend.users;

import com.documed.backend.FullDAO;
import com.documed.backend.ReadDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.users.model.Subscription;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SubscriptionDAO implements FullDAO<Subscription, Subscription> {

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

  @Override
  public Subscription create(Subscription creationObject) {
    String sql = "INSERT INTO subscription (name, price) VALUES (?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
            connection -> {
              PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
              ps.setString(1, creationObject.getName());
              ps.setBigDecimal(2, creationObject.getPrice());
              return ps;
            }, keyHolder
    );

    Number key = keyHolder.getKey();

    if (key != null) {
      creationObject.setId(key.intValue());
      return creationObject;
    } else {
      throw new CreationFailException("Failed create subscription");
    }
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM subscription WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }
}
