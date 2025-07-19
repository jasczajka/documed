package com.documed.backend.medicines;

import com.documed.backend.auth.annotations.AdminOnly;
import com.documed.backend.auth.annotations.DoctorOnly;
import com.documed.backend.medicines.model.Medicine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicines")
@AllArgsConstructor
public class MedicineController {
  private final MedicineService medicineService;
  private final MedicineImportService medicineImportService;

  @DoctorOnly
  @GetMapping("/search")
  @Operation(summary = "Search medicines (lite version for async select)")
  public List<Medicine> searchMedicines(
      @Parameter(description = "Search query") @RequestParam(value = "q") String query,
      @Parameter(description = "Maximum results to return", example = "20")
          @RequestParam(value = "limit", defaultValue = "20")
          int limit) {
    return medicineService.search(query, limit);
  }

  @DoctorOnly
  @GetMapping
  @Operation(summary = "Get all medicines (full version)")
  public List<Medicine> getAllMedicines(
      @Parameter(description = "Maximum results to return", example = "20")
          @RequestParam(value = "limit", defaultValue = "500")
          int limit) {
    return medicineService.getLimited(limit);
  }

  @DoctorOnly
  @GetMapping("/{id}")
  @Operation(summary = "Get medicine by ID")
  public Optional<Medicine> getMedicine(
      @Parameter(
              description = "Medicine ID",
              required = true,
              schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
          @PathVariable
          String id) {
    return medicineService.getById(id);
  }

  @AdminOnly
  @PostMapping("/import")
  @Operation(summary = "Import medicines from datasource")
  public ResponseEntity<String> importMedicines() {
    medicineImportService.importMedicinesWeekly();
    return new ResponseEntity<>("Medicines imported", HttpStatus.CREATED);
  }
}
