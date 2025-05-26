package com.documed.backend.users;

import com.documed.backend.FullDAO;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.exceptions.SubscriptionAssignmentException;
import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDAO implements FullDAO<User, User> {

  private final JdbcTemplate jdbcTemplate;

  private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

  public UserDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private RowMapper<User> userRowMapper =
      (rs, rowNum) ->
          User.builder()
              .id(rs.getInt("id"))
              .firstName(rs.getString("first_name"))
              .lastName(rs.getString("last_name"))
              .email(rs.getString("email"))
              .address(rs.getString("address"))
              .password(rs.getString("password"))
              .accountStatus(AccountStatus.valueOf(rs.getString("account_status")))
              .role(UserRole.valueOf(rs.getString("role")))
              .birthDate(rs.getDate("birthdate"))
              .emailNotifications(rs.getBoolean("email_notifications"))
              // Optional fields
              .pesel(rs.getString("pesel"))
              .phoneNumber(rs.getString("phone_number"))
              .pwzNumber(rs.getString("pwz"))
              .subscriptionId(rs.getInt("subscription_id"))
              .build();

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
  public User create(User user) {
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

  public User update(User user) {
    String sql =
        "UPDATE \"User\" SET first_name = ?, last_name = ?, email = ?, address = ?, "
            + "password = ?, account_status = ?, role = ?, "
            + "pesel = ?, phone_number = ?, birthdate = ?, pwz = ? "
            + "WHERE id = ?";

    int rowsAffected =
        jdbcTemplate.update(
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

    if (rowsAffected == 0) {
      throw new IllegalStateException("Failed to update user with id " + user.getId());
    }

    return getById(user.getId())
        .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated user"));
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

  @Transactional
  public User updateUserSpecializations(int doctorId, List<Integer> newSpecIds) {
    String selectSql = "SELECT specialization_id FROM doctor_specialization WHERE doctor_id = ?";
    List<Integer> currentSpecIds = jdbcTemplate.queryForList(selectSql, Integer.class, doctorId);

    Set<Integer> toAdd = new HashSet<>(newSpecIds);
    Set<Integer> toRemove = new HashSet<>(currentSpecIds);

    toAdd.removeAll(currentSpecIds);
    toRemove.removeAll(newSpecIds);

    if (!toRemove.isEmpty()) {
      String deleteSql =
          "DELETE FROM doctor_specialization WHERE doctor_id = ? AND specialization_id = ?";
      jdbcTemplate.batchUpdate(
          deleteSql,
          toRemove,
          toRemove.size(),
          (ps, specId) -> {
            ps.setInt(1, doctorId);
            ps.setInt(2, specId);
          });
    }

    if (!toAdd.isEmpty()) {
      String insertSql =
          """
              INSERT INTO doctor_specialization (doctor_id, specialization_id)
              VALUES (?, ?)
              ON CONFLICT DO NOTHING
              """;
      jdbcTemplate.batchUpdate(
          insertSql,
          toAdd,
          toAdd.size(),
          (ps, specId) -> {
            ps.setInt(1, doctorId);
            ps.setInt(2, specId);
          });
    }

    return getById(doctorId)
        .orElseThrow(() -> new UserNotFoundException("Doctor not found: " + doctorId));
  }

  public List<Specialization> getUserSpecializationsById(int userId) {
    String sql =
        """
        SELECT s.id, s.name
        FROM doctor_specialization ds
        JOIN Specialization s ON ds.specialization_id = s.id
        WHERE ds.doctor_id = ?
    """;

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            Specialization.builder().id(rs.getInt("id")).name(rs.getString("name")).build(),
        userId);
  }

  @Transactional
  public User addSpecializationsToUser(int doctorId, List<Integer> specIds) {
    String sql =
        """
      INSERT INTO doctor_specialization (doctor_id, specialization_id)
      VALUES (?, ?)
      ON CONFLICT DO NOTHING
      """;

    int[][] results =
        jdbcTemplate.batchUpdate(
            sql,
            specIds,
            specIds.size(),
            (ps, specId) -> {
              ps.setInt(1, doctorId);
              ps.setInt(2, specId);
            });

    int total = Arrays.stream(results).flatMapToInt(Arrays::stream).sum();
    if (total == 0) {
      logger.debug("No new specializations were added for doctor {}", doctorId);
    }

    return getById(doctorId)
        .orElseThrow(() -> new UserNotFoundException("Doctor not found: " + doctorId));
  }

  public void updatePasswordByEmail(String email, String encodedPassword) {
    String sql = "UPDATE \"User\" SET password = ? WHERE email = ?";
    int rowsAffected = jdbcTemplate.update(sql, encodedPassword, email);

    if (rowsAffected == 0) {
      throw new UserNotFoundException("No user found with email: " + email);
    }
  }

  public void updatePasswordById(int userId, String encodedPassword) {
    String sql = "UPDATE \"User\" SET password = ? WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, encodedPassword, userId);

    if (rowsAffected == 0) {
      throw new UserNotFoundException("User not found with ID: " + userId);
    }
  }

  public void toggleEmailNotificationsById(int userId) {
    String sql = "UPDATE \"User\" SET email_notifications = NOT email_notifications WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, userId);

    if (rowsAffected == 0) {
      throw new UserNotFoundException("User not found with ID: " + userId);
    }
  }

  public void updateUserSubscription(int userId, int subscriptionId) {
    int rowsAffected;
    String sql = "UPDATE \"User\" SET subscription_id = ? WHERE id = ?";

    if (subscriptionId == 0) {
      sql = "UPDATE \"User\" SET subscription_id = NULL WHERE id = ?";
      rowsAffected = jdbcTemplate.update(sql, userId);
    } else {
      rowsAffected = jdbcTemplate.update(sql, subscriptionId, userId);
    }

    if (rowsAffected != 1) {
      throw new SubscriptionAssignmentException("Failed to assign subscription");
    }
  }

  @Transactional
  public Integer deletePatientPersonalData(int patientId) {
    User user =
        getById(patientId)
            .orElseThrow(
                () -> new UserNotFoundException("Patient not found with ID: " + patientId));

    if (user.getRole() != UserRole.PATIENT) {
      throw new IllegalArgumentException("Only patient accounts can be processed this way");
    }

    String sql =
        """
        UPDATE "User"
        SET
            first_name = '[deleted]',
            last_name = '[deleted]',
            email = CONCAT('deleted_', id, '@example.com'),
            address = '[deleted]',
            pesel = NULL,
            phone_number = NULL,
            account_status = 'DEACTIVATED',
            password = '[deleted]'
        WHERE id = ?
        """;

    return jdbcTemplate.update(sql, patientId);
  }
}
