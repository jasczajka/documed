package com.documed.backend.attachments;

import com.documed.backend.FullDAO;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.attachments.model.AttachmentStatus;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AttachmentDAO implements FullDAO<Attachment, Attachment> {
  private final JdbcTemplate jdbcTemplate;

  public AttachmentDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<Attachment> rowMapper =
      (rs, rowNum) ->
          Attachment.builder()
              .id(rs.getInt("id"))
              .fileName(rs.getString("file_name"))
              .s3Key(rs.getString("s3_key"))
              .status(AttachmentStatus.valueOf(rs.getString("status")))
              .visitId((Integer) rs.getObject("visit_id"))
              .additionalServiceId((Integer) rs.getObject("additional_service_id"))
              .build();

  @Override
  public Attachment create(Attachment attachment) {
    String sql =
        """
            INSERT INTO attachment
              (file_name, s3_key,status, visit_id, additional_service_id )
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
        conn -> {
          PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, attachment.getFileName());
          ps.setString(2, attachment.getS3Key());
          ps.setString(3, attachment.getStatus().toString());
          ps.setObject(4, attachment.getVisitId(), Types.INTEGER);
          ps.setObject(5, attachment.getAdditionalServiceId(), Types.INTEGER);

          return ps;
        },
        kh);
    Number key = kh.getKey();
    if (key != null) {
      attachment.setId(key.intValue());
      return attachment;
    } else {
      throw new IllegalStateException("Failed to retrieve id value");
    }
  }

  @Override
  public Optional<Attachment> getById(int id) {
    String sql = "SELECT * FROM attachment WHERE id = ?";
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  public Optional<Attachment> getUploadedById(int id) {
    String sql = "SELECT * FROM attachment WHERE id = ? AND status = 'UPLOADED'";
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  public boolean setAttachmentAsUploaded(int id) {
    String sql = "UPDATE  attachment SET status = 'UPLOADED' WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    if (rowsAffected == 0) {
      return false;
    }
    return true;
  }

  @Override
  public int delete(int id) {
    String sql = "DELETE FROM attachment WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public List<Attachment> getAll() {
    String sql = "SELECT * FROM attachment";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<Attachment> getUploadedByVisit(int visitId) {
    String sql = "SELECT * FROM attachment WHERE visit_id = ? AND status = 'UPLOADED'";
    return jdbcTemplate.query(sql, rowMapper, visitId);
  }
}
