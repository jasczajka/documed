package com.documed.backend.medicines;

import com.documed.backend.FullDAO;
import com.documed.backend.medicines.model.Medicine;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MedicineDAO implements FullDAO<Medicine> {

  private final JdbcTemplate jdbcTemplate;

  public MedicineDAO(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<Medicine> rowMapper =
      (rs, rowNum) ->
          Medicine.builder()
              .id(rs.getString("id"))
              .name(rs.getString("name"))
              .commonName(rs.getString("common_name"))
              .dosage(rs.getString("dosage"))
              .build();

  public Optional<Medicine> getById(int id) {
    throw new UnsupportedOperationException();
  }

  public Optional<Medicine> getById(String id) {
    String sql = "SELECT * FROM medicine WHERE id = ?";
    return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
  }

  public Medicine createOrUpdate(Medicine medicine) {
    String sql =
        """
            INSERT INTO medicine (id, name, power, common_name, packaging)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              name = EXCLUDED.name,
              common_name = EXCLUDED.common_name,
              dosage = EXCLUDED.dosage,
        """;

    jdbcTemplate.update(
        sql, medicine.getId(), medicine.getName(), medicine.getCommonName(), medicine.getDosage());

    return medicine;
  }

  @Override
  public List<Medicine> getAll() {
    return jdbcTemplate.query("SELECT * FROM medicine", rowMapper);
  }

  @Override
  public Medicine create(Medicine medicine) {
    return createOrUpdate(medicine);
  }

  public Medicine update(Medicine medicine) {
    return createOrUpdate(medicine);
  }

  @Override
  public int delete(int id) {
    return jdbcTemplate.update("DELETE FROM medicine WHERE id = ?", id);
  }

  public List<Medicine> getLimited(int limit) {
    String sql = "SELECT * FROM medicine LIMIT ?";
    return jdbcTemplate.query(sql, rowMapper, limit);
  }

  public List<Medicine> search(String query, int limit) {
    String sql =
        """
            SELECT id, name, common_name, dosage FROM medicine
            WHERE LOWER(name) LIKE LOWER(?) OR LOWER(common_name) LIKE LOWER(?)
            LIMIT ?
            """;
    String likeQuery = "%" + query + "%";
    return jdbcTemplate.query(sql, rowMapper, likeQuery, likeQuery, limit);
  }
}
