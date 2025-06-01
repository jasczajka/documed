package com.documed.backend.referrals;

import com.documed.backend.FullDAO;
import com.documed.backend.exceptions.CreationFailException;
import com.documed.backend.referrals.model.CreateReferralDTO;
import com.documed.backend.referrals.model.Referral;
import com.documed.backend.referrals.model.ReferralType;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReferralDAO implements FullDAO<Referral, CreateReferralDTO> {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Referral> rowMapper =
      (rs, rowNum) ->
          Referral.builder()
              .id(rs.getInt("id"))
              .diagnosis(rs.getString("diagnosis"))
              .type(ReferralType.valueOf(rs.getString("type")))
              .expirationDate(rs.getDate("expiration_date").toLocalDate())
              .visitId(rs.getInt("visit_id"))
              .build();

  @Override
  public Referral create(CreateReferralDTO createReferralDTO) {
    String sql =
        "INSERT INTO referral (diagnosis, type, expiration_date, visit_id) VALUES (?, ?, ?, ?) RETURNING id";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    try {
      jdbcTemplate.update(
          connection -> {
            PreparedStatement ps =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, createReferralDTO.getDiagnosis());
            ps.setString(2, createReferralDTO.getType().name());
            ps.setDate(3, java.sql.Date.valueOf(createReferralDTO.getExpirationDate()));
            ps.setInt(4, createReferralDTO.getVisitId());
            return ps;
          },
          keyHolder);
    } catch (DataIntegrityViolationException e) {
      throw new CreationFailException("Failed to create referral due to wrong input data");
    }

    Number key = keyHolder.getKey();

    if (key != null) {
      return Referral.builder()
          .id(key.intValue())
          .visitId(createReferralDTO.getVisitId())
          .diagnosis(createReferralDTO.getDiagnosis())
          .type(createReferralDTO.getType())
          .expirationDate(createReferralDTO.getExpirationDate())
          .build();
    } else {
      throw new CreationFailException("Failed to create referral");
    }
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM referral WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public Optional<Referral> getById(int id) {
    String sql = "SELECT * FROM referral WHERE id = ?";
    List<Referral> referrals = jdbcTemplate.query(sql, rowMapper, id);
    return referrals.stream().findFirst();
  }

  @Override
  public List<Referral> getAll() {
    String sql = "SELECT * FROM referral";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<Referral> getReferralsForVisit(int visitId) {
    String sql = "SELECT * FROM referral WHERE visit_id = ?";
    return jdbcTemplate.query(sql, rowMapper, visitId);
  }

  public List<Referral> getReferralsForPatient(int patientId) {
    String sql =
        """
            SELECT * FROM referral
            JOIN visit ON referral.visit_id = visit.id
            WHERE visit.patient_id = ?;
        """;
    return jdbcTemplate.query(sql, rowMapper, patientId);
  }
}
