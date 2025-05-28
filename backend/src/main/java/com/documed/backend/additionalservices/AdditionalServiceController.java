package com.documed.backend.additionalservices;

import com.documed.backend.additionalservices.dtos.*;
import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.attachments.S3Service;
import com.documed.backend.attachments.dtos.FileInfoDTO;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.ServiceService;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.model.User;
import com.documed.backend.users.services.UserService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/additional_services")
@RequiredArgsConstructor
public class AdditionalServiceController {
  private final AdditionalServiceService additionalServiceService;
  private final UserService userService;
  private final ServiceService serviceService;
  private final S3Service s3service;
  private static final Logger log = LoggerFactory.getLogger(AdditionalServiceController.class);

  @StaffOnly
  @PostMapping
  public ResponseEntity<AdditionalServiceReturnDTO> createAdditionalService(
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
        .body(this.enrichAdditionalServiceToDto(additionalServiceWithAttachments));
  }

  @StaffOnly
  @GetMapping("/{id}")
  public ResponseEntity<AdditionalServiceReturnDTO> getAdditionalService(@PathVariable int id) {
    AdditionalService additionalService = additionalServiceService.getById(id);

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.enrichAdditionalServiceToDto(additionalService));
  }

  @StaffOnly
  @GetMapping("/fulfillers/{userId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getAdditionalServicesByFulfiller(
      @PathVariable int userId) {
    List<AdditionalService> services = additionalServiceService.getByFulfiller(userId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @GetMapping("/services/{serviceId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getAdditionalServicesByService(
      @PathVariable int serviceId) {
    List<AdditionalService> services = additionalServiceService.getByService(serviceId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnlyOrSelf
  @GetMapping("/patients/{userId}")
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getAdditionalServicesByPatient(
      @PathVariable int userId) {
    List<AdditionalService> services = additionalServiceService.getByPatient(userId);
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @GetMapping
  public ResponseEntity<List<AdditionalServiceReturnDTO>> getAllAdditionalServices() {
    List<AdditionalService> services = additionalServiceService.getAll();
    List<AdditionalServiceReturnDTO> dtos =
        services.stream().map(this::enrichAdditionalServiceToDto).toList();
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @StaffOnly
  @PutMapping("/{id}/description")
  public ResponseEntity<Void> updateAdditionalServiceDescription(
      @PathVariable int id, @RequestBody @Valid UpdateDescriptionDTO createDto) {
    additionalServiceService.updateDescription(id, createDto.getDescription());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @StaffOnly
  @PutMapping("/{id}/attachments")
  public ResponseEntity<AdditionalServiceReturnDTO> updateAdditionalServiceAttachments(
      @PathVariable int id, @RequestBody @Valid UpdateAttachmentsDTO updateDto) {
    AdditionalService updatedService =
        additionalServiceService.updateAttachmentsForAdditionalService(
            id, updateDto.getAttachmentIds());

    return ResponseEntity.ok(this.enrichAdditionalServiceToDto(updatedService));
  }

  private AdditionalServiceReturnDTO enrichAdditionalServiceToDto(
      AdditionalService additionalService) {
    List<FileInfoDTO> fileInfoDTOS =
        s3service.generateFileInfosForAdditionalService(additionalService.getId());

    User patient =
        userService
            .getById(additionalService.getPatientId())
            .orElseThrow(
                () -> {
                  log.warn("Patient not found with ID: {}", additionalService.getPatientId());
                  return new NotFoundException("Patient not found");
                });

    User fulfiller =
        userService
            .getById(additionalService.getFulfillerId())
            .orElseThrow(
                () -> {
                  log.warn("Fulfiller not found with ID: {}", additionalService.getFulfillerId());
                  return new NotFoundException("Fulfiller not found");
                });
    Service service =
        serviceService
            .getById(additionalService.getServiceId())
            .orElseThrow(
                () -> {
                  log.warn("Service not found with ID: {}", additionalService.getServiceId());
                  return new NotFoundException("Service not found");
                });

    return AdditionalServiceMapper.toDto(
        additionalService, patient, fulfiller, service, fileInfoDTOS);
  }
}
