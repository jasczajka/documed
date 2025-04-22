package com.documed.backend.services;

import com.documed.backend.FullDAO;
import com.documed.backend.users.SpecializationDAO;
import com.documed.backend.users.model.Specialization;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceDAO implements FullDAO<Service, Service> {

  private final JdbcTemplate jdbcTemplate;
  private final SpecializationDAO specializationDAO;

  public ServiceDAO(JdbcTemplate jdbcTemplate, SpecializationDAO specializationDAO) {
    this.jdbcTemplate = jdbcTemplate;
    this.specializationDAO = specializationDAO;
  }

  @Override
  public Service create(Service obj) {
    String sql =
        "INSERT INTO service (name, price, type, estimated_time) VALUES (?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, obj.getName());
          ps.setBigDecimal(2, obj.getPrice());
          ps.setString(3, obj.getType().name());
          ps.setInt(4, obj.getEstimatedTime());
          return ps;
        },
        keyHolder);

    if (keyHolder.getKey() != null) {
      obj.setId(keyHolder.getKey().intValue());
    } else {
      throw new RuntimeException("Failed retrieve id value");
    }

    return obj;
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM service WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public Optional<Service> getById(int id) {
    String sql = "SELECT * FROM service WHERE id = ?";

    List<Service> services =
        jdbcTemplate.query(
            sql,
            (rs, rowNum) ->
                Service.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .price(rs.getBigDecimal("price"))
                    .type(ServiceType.valueOf(rs.getString("type")))
                    .estimatedTime(rs.getInt("estimated_time"))
                    .build(),
            id);

    return services.stream().findFirst();
  }

  @Override
  public List<Service> getAll() {
    String sql = "SELECT * FROM service";

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) -> {
          int id = rs.getInt("id");
          String name = rs.getString("name");
          BigDecimal price = rs.getBigDecimal("price");
          ServiceType type = ServiceType.valueOf(rs.getString("type"));
          int estimatedTime = rs.getInt("estimated_time");
          return Service.builder()
              .id(id)
              .name(name)
              .price(price)
              .type(type)
              .estimatedTime(estimatedTime)
              .build();
        });
  }

  public Service updatePrice(int id, BigDecimal price) {
    String sql = "UPDATE service SET price = ? WHERE id = ?";
    int affectedRows = jdbcTemplate.update(sql, price, id);

    if (affectedRows == 1) {
      return getById(id).orElseThrow(RuntimeException::new);
    } else {
      throw new RuntimeException("Failed to update price");
    }
  }

  public Service updateEstimatedTime(int id, int estimatedTime) {
    String sql = "UPDATE service SET estimated_time = ? WHERE id = ?";

    int affectedRows = jdbcTemplate.update(sql, estimatedTime, id);

    if (affectedRows == 1) {
      return getById(id).orElseThrow(RuntimeException::new);
    } else {
      throw new RuntimeException("Failed to update estimated time");
    }
  }

  public Specialization addSpecializationToService(int serviceId, int specializationId) {
    String sql = "INSERT INTO specialization_service (service_id, specialization_id) VALUES (?, ?)";

    int affectedRows = jdbcTemplate.update(sql, serviceId, specializationId);

    if (affectedRows == 1) {
      return specializationDAO.getById(specializationId).orElseThrow(RuntimeException::new);
    } else {
      throw new RuntimeException("Failed to add specialization to the service");
    }
  }

  public Service addSpecializationsToService(int serviceId, List<Integer> specializationIds) {
    String sql =
        "INSERT INTO specialization_service (specialization_id, service_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

    int[][] affectedMatrix =
        jdbcTemplate.batchUpdate(
            sql,
            specializationIds,
            specializationIds.size(),
            (ps, specializationId) -> {
              ps.setInt(1, specializationId);
              ps.setInt(2, serviceId);
            });

    int totalAffected = Arrays.stream(affectedMatrix).flatMapToInt(Arrays::stream).sum();
    if (totalAffected >= 1) {
      return getById(serviceId).orElseThrow(RuntimeException::new);
    } else {
      throw new RuntimeException("Failed to add specializations to the service");
    }
  }

  public int removeSpecializationFromService(int serviceId, int specializationId) {
    String sql =
        "DELETE FROM specialization_service WHERE service_id = ? AND specialization_id = ?";
    return jdbcTemplate.update(sql, serviceId, specializationId);
  }
}
