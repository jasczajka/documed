package com.documed.backend.users;

import com.documed.backend.FullDAO;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO implements FullDAO<User> {

  private final JdbcTemplate jdbcTemplate;

  public UserDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private RowMapper<User> userRowMapper =
      (rs, rowNum) -> {
        return User.builder()
            .id(rs.getInt("id"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .address(rs.getString("address"))
            .password(rs.getString("password"))
            .accountStatus(AccountStatus.valueOf(rs.getString("account_status")))
            .role(UserRole.valueOf(rs.getString("role")))
            .birthDate(rs.getDate("birthdate"))
            // Optional fields
            .pesel(rs.getString("pesel"))
            .phoneNumber(rs.getString("phone_number"))
            .pwzNumber(rs.getString("pwz"))
            .build();
      };

  @Override
  public Optional<User> getById(int id) {
    String sql = "SELECT * FROM \"User\" WHERE id = ?";
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, id));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<User> getAll() {
    String sql = "SELECT * FROM \"User\"";
    return jdbcTemplate.query(sql, userRowMapper);
  }

  @Override
  public int create(User user) {
    String sql =
        "INSERT INTO \"User\" (first_name, last_name, email, address, password, status, role, "
            + "pesel, phone_number, birthdate, pwz) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    return jdbcTemplate.update(
        sql,
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getAddress(),
        user.getPassword(),
        user.getAccountStatus(),
        user.getRole().toString(),
        user.getPesel(),
        user.getPhoneNumber(),
        user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null,
        user.getPwzNumber());
  }

  @Override
  public int update(User user) {
    String sql =
        "UPDATE \"User\" SET first_name = ?, last_name = ?, email = ?, address = ?, "
            + "password = ?, status = ?, role = ?, "
            + "pesel = ?, phone_number = ?, birthdate = ?, pwz = ? "
            + "WHERE id = ?";

    return jdbcTemplate.update(
        sql,
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getAddress(),
        user.getPassword(),
        user.getAccountStatus().toString(),
        user.getRole().toString(),
        user.getPesel(),
        user.getPhoneNumber(),
        user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null,
        user.getPwzNumber(),
        user.getId());
  }

  @Override
  public int delete(int id) {
    String sql = "UPDATE \"User\" SET account_status = 'DEACTIVATED' WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  public Optional<User> getByEmail(String email) {
    String sql = "SELECT * FROM \"User\" WHERE email = ?";
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, email));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> getByPesel(String pesel) {
    String sql = "SELECT * FROM \"User\" WHERE pesel = ?";
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, pesel));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public User createAndReturn(User user) {
    String sql =
        "INSERT INTO \"User\" (first_name, last_name, email, address, password, account_status, role, "
            + "pesel, phone_number, birthdate, pwz) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "RETURNING id";

    Integer generatedId =
        jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> rs.getInt("id"),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getAddress(),
            user.getPassword(),
            user.getAccountStatus().toString(),
            user.getRole().toString(),
            user.getPesel(),
            user.getPhoneNumber(),
            user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null,
            user.getPwzNumber());

    return getById(generatedId)
        .orElseThrow(() -> new IllegalStateException("Failed to retrieve created user"));
  }
}
