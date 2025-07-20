package com.documed.backend.medicines;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class MedicineImportService {

  @Value("${medicine_list_file_url}")
  private String FILE_URL;

  private final JdbcTemplate jdbcTemplate;

  public MedicineImportService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final Path LOCAL_FILE_PATH = Path.of("data", "medicinal-products.xlsx");

  private static final Logger logger = LoggerFactory.getLogger(MedicineImportService.class);

  @Scheduled(cron = "0 0 23 * * SAT") // every Saturday at 23:00
  public void importMedicinesWeekly() {
    try {
      downloadFile();
      importMedicines();
    } catch (Exception e) {
      logger.error("Medicine import failed: {}", e.getMessage());
    }
  }

  private void downloadFile() throws IOException, URISyntaxException, InterruptedException {
    Files.createDirectories(LOCAL_FILE_PATH.getParent());

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(FILE_URL)).build();

    HttpResponse<InputStream> response =
        client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    try (InputStream in = response.body()) {
      Files.copy(in, LOCAL_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
      logger.info("Xlsx medicine file downloaded to: {}", LOCAL_FILE_PATH.toAbsolutePath());
    }
  }

  private void importMedicines() {
    long startTime = System.currentTimeMillis();
    logger.info("Starting medicine import...");

    TransactionTemplate transactionTemplate =
        new TransactionTemplate(new DataSourceTransactionManager(jdbcTemplate.getDataSource()));

    transactionTemplate.execute(
        status -> {
          try (InputStream fileIn = Files.newInputStream(LOCAL_FILE_PATH);
              Workbook workbook =
                  StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            AtomicInteger processed = new AtomicInteger(0);
            AtomicInteger skipped = new AtomicInteger(0);

            List<Row> rows = new ArrayList<>();
            Iterator<Row> iterator = sheet.iterator();
            if (iterator.hasNext()) iterator.next(); // Skip header row
            iterator.forEachRemaining(rows::add);

            String upsertSql =
                """
                    INSERT INTO medicine (id, name, common_name, dosage)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT (id) DO UPDATE SET
                        name = EXCLUDED.name,
                        common_name = EXCLUDED.common_name,
                        dosage = EXCLUDED.dosage
                    """;

            int batchSize = 500;
            for (int i = 0; i < rows.size(); i += batchSize) {
              List<Row> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));

              jdbcTemplate.batchUpdate(
                  upsertSql,
                  new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int index) throws SQLException {
                      Row row = batch.get(index);
                      try {
                        ps.setString(1, getStringValue(row.getCell(0))); // id - column 1
                        ps.setString(
                            2,
                            trimToMaxLength(
                                getStringValue(row.getCell(1)), 500)); // name - column 2
                        ps.setString(
                            3,
                            trimToMaxLength(
                                getStringValue(row.getCell(2)), 500)); // common name - column 3
                        ps.setString(
                            4,
                            trimToMaxLength(
                                getStringValue(row.getCell(7)), 100)); // dosage - column 8
                        processed.incrementAndGet();
                      } catch (Exception e) {
                        logger.warn("Skipping row {}: {}", row.getRowNum(), e.getMessage());
                        skipped.incrementAndGet();
                        ps.setString(1, "");
                        ps.setString(2, "");
                        ps.setString(3, "");
                        ps.setString(4, "");
                      }
                    }

                    @Override
                    public int getBatchSize() {
                      return batch.size();
                    }
                  });
            }

            logger.info("Import completed in {} ms", System.currentTimeMillis() - startTime);
            logger.info("Processed: {}, Skipped: {}, Total: {}", processed, skipped, totalRows);

          } catch (Exception e) {
            status.setRollbackOnly();
            logger.error("Import failed", e);
            throw new RuntimeException("Import failed", e);
          }
          return null;
        });
  }

  private String getStringValue(Cell cell) {
    if (cell == null) return "-";
    return switch (cell.getCellType()) {
      case STRING -> {
        String value = cell.getStringCellValue().trim();
        yield value.isEmpty() ? "-" : value;
      }
      case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      case BLANK -> "-";
      default -> "-";
    };
  }

  private String trimToMaxLength(String value, int maxLength) {
    if (value == null) return "";
    return value.length() > maxLength ? value.substring(0, maxLength) : value;
  }
}
