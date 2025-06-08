package com.documed.backend.additionalservices;

import com.documed.backend.additionalservices.dtos.*;
import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.additionalservices.model.AdditionalServiceWithDetails;
import com.documed.backend.attachments.S3Service;
import com.documed.backend.attachments.dtos.FileInfoDTO;
import com.documed.backend.auth.annotations.MedicalStaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/additional_services")
@RequiredArgsConstructor
public class AdditionalServiceController {
  private final AdditionalServiceService additionalServiceService;
  private final S3Service s3service;

  private static final Period DEFAULT_VISIT_LOOKBACK_PERIOD = Period.ofMonths(3);

  private LocalDate resolveStartDate(LocalDate inputStartDate) {
    return (inputStartDate != null)
        ? inputStartDate
        : LocalDate.now().minus(DEFAULT_VISIT_LOOKBACK_PERIOD);
  }

  private LocalDate resolveEndDate() {
    return LocalDate.now();
  }

  @MedicalStaffOnly
  @PostMapping
  public ResponseEntity<AdditionalServiceWithDetails> createAdditionalService(
      @RequestBody @Valid CreateAdditionalServiceDTO createDto) {
    AdditionalService additionalService =
        additionalServiceService.createAdditionalService(
            createDto.getDescription(),
            createDto.getDate(),
            createDto.getPatientId(),
            createDto.getFulfillerId(),
            createDto.getServiceId());

    List<Integer> attachmentIds = createDto.getAttachmentIds();
    if (attachmentIds == null) {
      attachmentIds = Collections.emptyList();
    }

    AdditionalService additionalServiceWithAttachments =
        additionalServiceService.updateAttachmentsForAdditionalService(
            additionalService.getId(), attachmentIds);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            this.enrichAdditionalServiceToDto(
                additionalServiceService.getByIdWithDetails(
                    additionalServiceWithAttachments.getId())));
  }

  @MedicalStaffOnly
  @GetMapping("/{id}")
  public ResponseEntity<AdditionalServiceWithDetails> getAdditionalService(@PathVariable int id) {
    AdditionalServiceWithDetails additionalService =
        additionalServiceService.getByIdWithDetails(id);

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.enrichAdditionalServiceToDto(additionalService));
  }

  @MedicalStaffOnly
  @GetMapping("/fulfillers/{userId}")
  public ResponseEntity<List<AdditionalServiceWithDetails>> getAdditionalServicesByFulfiller(
      @PathVariable int userId, @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);
    LocalDate endDate = resolveEndDate();

    List<AdditionalServiceWithDetails> services =
        additionalServiceService.getByFulfillerIdWithDetailsBetweenDates(
            userId, resolvedStart, endDate);
    List<AdditionalServiceWithDetails> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @MedicalStaffOnly
  @GetMapping("/services/{serviceId}")
  public ResponseEntity<List<AdditionalServiceWithDetails>> getAdditionalServicesByService(
      @PathVariable int serviceId, @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);
    LocalDate endDate = resolveEndDate();

    List<AdditionalServiceWithDetails> services =
        additionalServiceService.getByServiceIdWithDetailsBetweenDates(
            serviceId, resolvedStart, endDate);
    List<AdditionalServiceWithDetails> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnlyOrSelf
  @GetMapping("/patients/{userId}")
  public ResponseEntity<List<AdditionalServiceWithDetails>> getAdditionalServicesByPatient(
      @PathVariable int userId, @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);
    LocalDate endDate = resolveEndDate();

    List<AdditionalServiceWithDetails> services =
        additionalServiceService.getByPatientIdWithDetailsBetweenDates(
            userId, resolvedStart, endDate);
    List<AdditionalServiceWithDetails> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @MedicalStaffOnly
  @GetMapping
  public ResponseEntity<List<AdditionalServiceWithDetails>> getAllAdditionalServices(
      @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);
    LocalDate endDate = resolveEndDate();

    List<AdditionalServiceWithDetails> services =
        additionalServiceService.getAllWithDetailsBetweenDates(resolvedStart, endDate);
    List<AdditionalServiceWithDetails> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @MedicalStaffOnly
  @PutMapping("/{id}/description")
  public ResponseEntity<Void> updateAdditionalServiceDescription(
      @PathVariable int id, @RequestBody @Valid UpdateDescriptionDTO createDto) {
    additionalServiceService.updateDescription(id, createDto.getDescription());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @MedicalStaffOnly
  @PutMapping("/{id}/attachments")
  public ResponseEntity<AdditionalServiceWithDetails> updateAdditionalServiceAttachments(
      @PathVariable int id, @RequestBody @Valid UpdateAttachmentsDTO updateDto) {
    additionalServiceService.updateAttachmentsForAdditionalService(
        id, updateDto.getAttachmentIds());

    return ResponseEntity.ok(
        this.enrichAdditionalServiceToDto(additionalServiceService.getByIdWithDetails(id)));
  }

  private AdditionalServiceWithDetails enrichAdditionalServiceToDto(
      AdditionalServiceWithDetails additionalServiceToEnrich) {
    List<FileInfoDTO> fileInfoDTOS =
        s3service.generateFileInfosForAdditionalService(additionalServiceToEnrich.getId());

    additionalServiceToEnrich.setAttachments(fileInfoDTOS);
    return additionalServiceToEnrich;
  }
}
