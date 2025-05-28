package com.documed.backend.attachments;

import com.documed.backend.FullDAO;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.attachments.model.AttachmentStatus;
import com.documed.backend.exceptions.InvalidAssignmentException;
import com.documed.backend.exceptions.NotFoundException;
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
              .sizeBytes(rs.getLong("size_bytes"))
              .status(AttachmentStatus.valueOf(rs.getString("status")))
              .visitId((Integer) rs.getObject("visit_id"))
              .additionalServiceId((Integer) rs.getObject("additional_service_id"))
              .build();

  @Override
  public Attachment create(Attachment attachment) {
    String sql =
        """
                  INSERT INTO attachment
                    (file_name, s3_key, size_bytes, status, visit_id, additional_service_id )
                  VALUES (?, ?, ?, ?, ?, ?)
                  RETURNING id
                  """;
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
        conn -> {
          PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, attachment.getFileName());
          ps.setString(2, attachment.getS3Key());
          ps.setLong(3, attachment.getSizeBytes());
          ps.setString(4, attachment.getStatus().toString());
          ps.setObject(5, attachment.getVisitId(), Types.INTEGER);
          ps.setObject(6, attachment.getAdditionalServiceId(), Types.INTEGER);

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
    String sql =
        """
                 SELECT id, file_name, s3_key, size_bytes, status, visit_id, additional_service_id FROM
                 attachment WHERE id = ?
                 """;
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  public Optional<Attachment> getUploadedById(int id) {
    String sql =
        """
                SELECT id, file_name, s3_key, size_bytes, status, visit_id, additional_service_id FROM attachment
                WHERE id = ?
                AND status = 'UPLOADED'
                 """;
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
    String sql =
        """
                SELECT id, file_name, s3_key, size_bytes, status, visit_id, additional_service_id
                FROM attachment
                """;
    return jdbcTemplate.query(sql, rowMapper);
  }

  public List<Attachment> getUploadedByVisitId(int visitId) {
    String sql =
        """
           SELECT id, file_name, s3_key, size_bytes, status, visit_id, additional_service_id
           FROM attachment WHERE visit_id = ? AND status = 'UPLOADED'
           """;
    return jdbcTemplate.query(sql, rowMapper, visitId);
  }

  public List<Attachment> getUploadedByAdditionalServiceId(int additionalServiceId) {
    String sql =
        """
                SELECT id, file_name, s3_key, size_bytes, status, visit_id, additional_service_id
                FROM attachment WHERE additional_service_id = ?
                 AND status = 'UPLOADED'
                 """;
    return jdbcTemplate.query(sql, rowMapper, additionalServiceId);
  }

  public List<Attachment> getUploadedByPatientId(int patientId) {
    String sql =
        """
                SELECT a.id, a.file_name, a.s3_key, a.size_bytes, a.status, a.visit_id, a.additional_service_id
                FROM attachment a
                LEFT JOIN visit v ON a.visit_id = v.id
                LEFT JOIN additional_service ads ON a.additional_service_id = ads.id
                WHERE a.status = 'UPLOADED'
                  AND (v.patient_id = ? OR ads.patient_id = ?)
                """;
    return jdbcTemplate.query(sql, rowMapper, patientId, patientId);
  }

  public Attachment assignToVisit(int attachmentId, int visitId) {
    Attachment attachment =
        getById(attachmentId)
            .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));

    validateAttachmentAssignment(attachment);

    String sql = "UPDATE attachment SET visit_id = ? WHERE id = ?";
    int updated = jdbcTemplate.update(sql, visitId, attachmentId);

    if (updated == 0) {
      throw new IllegalStateException("Failed to assign attachment to visit");
    }

    return getById(attachmentId)
        .orElseThrow(() -> new IllegalStateException("Attachment not found after update"));
  }

  public Attachment assignToAdditionalService(int attachmentId, int additionalServiceId) {
    Attachment attachment =
        getById(attachmentId)
            .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));

    validateAttachmentAssignment(attachment);

    String sql = "UPDATE attachment SET additional_service_id = ? WHERE id = ?";
    int updated = jdbcTemplate.update(sql, additionalServiceId, attachmentId);

    if (updated == 0) {
      throw new IllegalStateException("Failed to assign attachment to additional service");
    }

    return getById(attachmentId)
        .orElseThrow(() -> new NotFoundException("Attachment not found after update"));
  }

  private void validateAttachmentAssignment(Attachment attachment) {
    if (attachment.getVisitId() != null && attachment.getAdditionalServiceId() != null) {
      throw new InvalidAssignmentException(
          "Attachment cannot be assigned to both a visit and additional service simultaneously");
    }
  }
}
