package com.documed.backend.auth;

import com.documed.backend.auth.model.Otp;
import com.documed.backend.auth.model.OtpPurpose;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OtpDAO {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Otp> rowMapper =
      (rs, rowNum) ->
          Otp.builder()
              .id(rs.getLong("id"))
              .email(rs.getString("email"))
              .otp(rs.getString("otp"))
              .purpose(OtpPurpose.valueOf(rs.getString("purpose")))
              .generatedAt(rs.getTimestamp("generated_at").toLocalDateTime())
              .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
              .attempts(rs.getInt("attempts"))
              .used(rs.getBoolean("used"))
              .build();

  public OtpDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Otp create(Otp otp) {
    String sql =
        """
                INSERT INTO otp (email, otp, purpose, generated_at, expires_at, attempts, used)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, otp.getEmail());
          ps.setString(2, otp.getOtp());
          ps.setString(3, otp.getPurpose().name());
          ps.setTimestamp(4, Timestamp.valueOf(otp.getGeneratedAt()));
          ps.setTimestamp(5, Timestamp.valueOf(otp.getExpiresAt()));
          ps.setInt(6, otp.getAttempts());
          ps.setBoolean(7, otp.isUsed());
          return ps;
        },
        keyHolder);

    Number key = keyHolder.getKey();
    if (key != null) {
      otp.setId(key.longValue());
      return otp;
    } else {
      throw new IllegalStateException("Failed to retrieve OTP id");
    }
  }

  public int update(Otp otp) {
    String sql =
        """
                UPDATE otp
                SET attempts = ?, used = ?
                WHERE id = ?
                """;
    return jdbcTemplate.update(sql, otp.getAttempts(), otp.isUsed(), otp.getId());
  }

  public Optional<Otp> findByEmailAndOtpAndPurpose(String email, String otp, OtpPurpose purpose) {
    String sql =
        """
                SELECT *
                FROM otp
                WHERE email = ? AND otp = ? AND purpose = ? AND used = false AND expires_at > ?
                """;
    List<Otp> results =
        jdbcTemplate.query(
            sql, rowMapper, email, otp, purpose.name(), Timestamp.valueOf(LocalDateTime.now()));
    return results.stream().findFirst();
  }

  public Optional<Otp> findLatestByEmailAndPurpose(String email, OtpPurpose purpose) {
    String sql =
        """
                SELECT *
                FROM otp
                WHERE email = ? AND purpose = ? AND used = false AND expires_at > ?
                ORDER BY generated_at DESC
                LIMIT 1
                """;
    List<Otp> results =
        jdbcTemplate.query(
            sql, rowMapper, email, purpose.name(), Timestamp.valueOf(LocalDateTime.now()));
    return results.stream().findFirst();
  }
}
