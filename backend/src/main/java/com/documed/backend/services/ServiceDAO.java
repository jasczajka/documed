package com.documed.backend.services;

import com.documed.backend.FullDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ServiceDAO implements FullDAO<Service> {

    private final JdbcTemplate jdbcTemplate;

    public ServiceDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public int create(Service obj) throws SQLException {
        String sql = "INSERT INTO service (name, price, type, estimated_time) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, obj.getName(), obj.getPrice(), obj.getType().name(), obj.getEstimatedTime());
    }

    @Override
    public int update(Service obj) throws SQLException {
        return 0;
    }

    @Override
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM service WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Service> getById(int id) throws SQLException {
        String sql = "SELECT * FROM service WHERE id = ?";

        List<Service> services =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> new Service(id, rs.getString("name"), rs.getBigDecimal("price"),
                                ServiceType.valueOf(rs.getString("type")), rs.getInt("estimated_time")),
                        id);
        return Optional.ofNullable(services.stream().findFirst().orElse(null));
    }

    @Override
    public List<Service> getAll() throws SQLException {
        String sql = "SELECT * FROM service";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    BigDecimal price = rs.getBigDecimal("price");
                    ServiceType type = ServiceType.valueOf(rs.getString("type"));
                    int estimatedTime = rs.getInt("estimated_time");
                    return new Service(id, name, price, type, estimatedTime);
                });

    }

    public int updatePrice(int id, BigDecimal price) {
        String sql = "UPDATE service SET price = ? WHERE id = ?";
        return jdbcTemplate.update(sql, price, id);
    }

    public int updateEstimatedTime(int id, int estimatedTime) {
        String sql = "UPDATE service SET estimated_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, estimatedTime, id);
    }

}
