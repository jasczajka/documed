package com.documed.backend.visits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FacilityDAO implements DAO<Facility> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FacilityDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Facility getById(int id) throws SQLException {
        String sql = "SELECT * FROM placowka WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                int id2 = rs.getInt("id");
                String address = rs.getString("adres");
                String city = rs.getString("miasto");
                return new Facility(id2, address, city);
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Facility> getAll() throws SQLException {
        String sql = "SELECT * FROM placowka";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id = rs.getInt("id");
            String address = rs.getString("adres");
            String city = rs.getString("miasto");
            return new Facility(id, address, city);
        });
    }

    @Override
    public int create(Facility obj) throws SQLException {
        return 0;
    }

    @Override
    public int update(Facility obj) throws SQLException {
        return 0;
    }

    @Override
    public int delete(int id) throws SQLException {
        return 0;
    }
}
