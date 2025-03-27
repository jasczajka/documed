package com.documed.backend.visits;

import com.documed.backend.ReadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FacilityDAO implements ReadDAO<Facility> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FacilityDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Facility getById(int id) throws SQLException {
        String sql = "SELECT * FROM facility WHERE id = ?";

        List<Facility> facilities = jdbcTemplate.query(sql, (rs, rowNum) -> new Facility(id, rs.getString("address"), rs.getString("city")), id);
        return facilities.stream().findFirst().orElse(null);
    }

    @Override
    public List<Facility> getAll() throws SQLException {
        String sql = "SELECT * FROM facility";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id = rs.getInt("id");
            String address = rs.getString("address");
            String city = rs.getString("city");
            return new Facility(id, address, city);
        });
    }
}


