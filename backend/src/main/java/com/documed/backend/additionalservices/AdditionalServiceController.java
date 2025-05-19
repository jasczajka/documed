package com.documed.backend.additionalservices;

import com.documed.backend.additionalservices.dtos.*;
import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/additional_services")
@AllArgsConstructor
public class AdditionalServiceController {
  private final AdditionalServiceService additionalServiceService;

  @StaffOnly
  @PostMapping
  public ResponseEntity<AdditionalService> createAdditionalService(
      @RequestBody @Valid CreateAdditionalServiceDTO createDto) {
    AdditionalService additionalService =
        additionalServiceService.createAdditionalService(
            createDto.getDescription(),
            createDto.getDate(),
            createDto.getFulfillerId(),
            createDto.getPatientId(),
            createDto.getServiceId());

    List<Integer> attachmentIds = createDto.getAttachmentIds();
    if (attachmentIds == null) {
      attachmentIds = Collections.emptyList();
    }

    AdditionalService additionalServiceWithAttachments =
        additionalServiceService.updateAttachmentsForAdditionalService(
            additionalService.getId(), attachmentIds);

    return ResponseEntity.status(HttpStatus.CREATED).body(additionalServiceWithAttachments);
  }

  @StaffOnly
  @GetMapping("/{id}")
  public ResponseEntity<AdditionalServiceReturnDTO> getAdditionalService(@PathVariable int id) {
    AdditionalService additionalService = additionalServiceService.getById(id);

    return ResponseEntity.status(HttpStatus.OK)
        .body(AdditionalServiceMapper.toDto(additionalService));
  }

  @StaffOnly
  @GetMapping("/by-fulfiller/{userId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getByFulfiller(@PathVariable int userId) {
    List<AdditionalService> services = additionalServiceService.getByFulfiller(userId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(AdditionalServiceMapper::toDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @GetMapping("/by-service/{serviceId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getByService(
      @PathVariable int serviceId) {
    List<AdditionalService> services = additionalServiceService.getByService(serviceId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(AdditionalServiceMapper::toDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnlyOrSelf
  @GetMapping("/by-patient/{userId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getByPatient(@PathVariable int userId) {
    List<AdditionalService> services = additionalServiceService.getByPatient(userId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(AdditionalServiceMapper::toDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @GetMapping
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getAllAdditionalServices() {
    List<AdditionalService> services = additionalServiceService.getAll();
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(AdditionalServiceMapper::toDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @PutMapping("/{id}/description")
  public ResponseEntity<?> updateDescription(
      @PathVariable int id, @RequestBody @Valid UpdateDescriptionDTO createDto) {
    additionalServiceService.updateDescription(id, createDto.getDescription());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @StaffOnly
  @PutMapping("/{id}/attachments")
  public ResponseEntity<AdditionalServiceReturnDTO> updateAttachments(
      @PathVariable int id, @RequestBody @Valid UpdateAttachmentsDTO updateDto) {
    AdditionalService updatedService =
        additionalServiceService.updateAttachmentsForAdditionalService(
            id, updateDto.getAttachmentIds());

    return ResponseEntity.ok(AdditionalServiceMapper.toDto(updatedService));
  }
}
