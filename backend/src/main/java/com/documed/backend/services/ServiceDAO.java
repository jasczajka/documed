package com.documed.backend.services;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.services.model.Service;
import com.documed.backend.services.model.ServiceType;
import com.documed.backend.users.SpecializationDAO;
import com.documed.backend.users.model.Specialization;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
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

      Number key = keyHolder.getKey();

    if (key != null) {
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
    String sql =
        """
      SELECT
        s.id AS service_id,
        s.name,
        s.price,
        s.type,
        s.estimated_time,
        ss.specialization_id
      FROM service s
      LEFT JOIN specialization_service ss ON s.id = ss.service_id
      WHERE s.id = ?
      """;

    List<Service> services =
        jdbcTemplate.query(
            sql,
            rs -> {
              if (!rs.next()) return List.of();

              int serviceId = rs.getInt("service_id");
              String name = rs.getString("name");
              BigDecimal price = rs.getBigDecimal("price");
              ServiceType type = ServiceType.valueOf(rs.getString("type"));
              int estimatedTime = rs.getInt("estimated_time");

              List<Integer> specializationIds = new ArrayList<>();
              do {
                int specId = rs.getInt("specialization_id");
                if (!rs.wasNull()) {
                  specializationIds.add(specId);
                }
              } while (rs.next());

              Service service =
                  Service.builder()
                      .id(serviceId)
                      .name(name)
                      .price(price)
                      .type(type)
                      .estimatedTime(estimatedTime)
                      .specializationIds(specializationIds)
                      .build();

              return List.of(service);
            },
            id);

    return services.stream().findFirst();
  }

  @Override
  public List<Service> getAll() {
    String sql =
        """
        SELECT
            s.id AS service_id,
            s.name,
            s.price,
            s.type,
            s.estimated_time,
            ss.specialization_id
        FROM service s
        LEFT JOIN specialization_service ss ON s.id = ss.service_id
        ORDER BY s.id
        """;

    return jdbcTemplate.query(
        sql,
        rs -> {
          Map<Integer, Service> services = new LinkedHashMap<>();

          while (rs.next()) {
            int serviceId = rs.getInt("service_id");
            Service service = services.get(serviceId);

            if (service == null) {
              service =
                  Service.builder()
                      .id(serviceId)
                      .name(rs.getString("name"))
                      .price(rs.getBigDecimal("price"))
                      .type(ServiceType.valueOf(rs.getString("type")))
                      .estimatedTime(rs.getInt("estimated_time"))
                      .specializationIds(new ArrayList<>())
                      .build();
              services.put(serviceId, service);
            }

            int specId = rs.getInt("specialization_id");
            if (!rs.wasNull()) {
              service.getSpecializationIds().add(specId);
            }
          }

          return new ArrayList<>(services.values());
        });
  }

  public List<Service> getAllRegular() {
    String sql =
        """
                SELECT
                    s.id AS service_id,
                    s.name,
                    s.price,
                    s.type,
                    s.estimated_time
                FROM service s
                WHERE s.type = 'REGULAR_SERVICE'
                """;

    return jdbcTemplate.query(
        sql,
        rs -> {
          Map<Integer, Service> services = new LinkedHashMap<>();

          while (rs.next()) {
            int serviceId = rs.getInt("service_id");
            Service service = services.get(serviceId);

            if (service == null) {
              service =
                  Service.builder()
                      .id(serviceId)
                      .name(rs.getString("name"))
                      .price(rs.getBigDecimal("price"))
                      .type(ServiceType.valueOf(rs.getString("type")))
                      .estimatedTime(rs.getInt("estimated_time"))
                      .build();
              services.put(serviceId, service);
            }
          }

          return new ArrayList<>(services.values());
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
      throw new CreationFailException("Failed to add specialization to the service");
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
      throw new CreationFailException("Failed to add specializations to the service");
    }
  }

  public int removeSpecializationFromService(int serviceId, int specializationId) {
    String sql =
        "DELETE FROM specialization_service WHERE service_id = ? AND specialization_id = ?";
    return jdbcTemplate.update(sql, serviceId, specializationId);
  }

  public void removeAllSpecializationFromService(int serviceId) {
    String sql = "DELETE FROM specialization_service WHERE service_id = ?";
    jdbcTemplate.update(sql, serviceId);
  }
}
