package com.documed.backend.attachments;

import com.documed.backend.attachments.dtos.CompleteUploadRequestDTO;
import com.documed.backend.attachments.dtos.FileInfoDTO;
import com.documed.backend.attachments.dtos.GenerateUploadUrlRequestDTO;
import com.documed.backend.attachments.dtos.UploadUrlResponseDTO;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.attachments.model.AttachmentStatus;
import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.visits.VisitService;
import com.documed.backend.visits.model.Visit;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachments")
@AllArgsConstructor
public class AttachmentController {
  private final S3Service s3Service;
  private final AuthService authService;
  private final VisitService visitService;

  @PostMapping("/start-upload")
  public ResponseEntity<UploadUrlResponseDTO> generateUploadUrl(
      @Valid @RequestBody GenerateUploadUrlRequestDTO request) {

    String objectKey = UUID.randomUUID() + "-" + request.getFileName();

    Attachment attachment =
        Attachment.builder()
            .s3Key(objectKey)
            .fileName(request.getFileName())
            .visitId(request.getVisitId())
            .additionalServiceId(request.getAdditionalServiceId())
            .status(AttachmentStatus.PENDING)
            .sizeBytes(request.getSizeBytes())
            .build();

    Attachment createdAttachment = s3Service.createAttachment(attachment);

    String uploadUrl = s3Service.generatePreSignedPutUrl(objectKey, request.getSizeBytes());

    UploadUrlResponseDTO response =
        UploadUrlResponseDTO.builder()
            .uploadUrl(uploadUrl)
            .s3Key(objectKey)
            .attachmentId(createdAttachment.getId())
            .build();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/complete-upload")
  public ResponseEntity<String> completeUpload(
      @Valid @RequestBody CompleteUploadRequestDTO request) {
    s3Service.completeFileUpload(request.getAttachmentId());

    String downloadUrl = s3Service.generatePresignedGetUrl(request.getS3Key());

    return ResponseEntity.status(HttpStatus.OK).body(downloadUrl);
  }

  @GetMapping("/{id}")
  public ResponseEntity<String> getDownloadUrl(@PathVariable int id) {
    Attachment attachment = s3Service.getUploadedById(id);

    if (attachment.getStatus() != AttachmentStatus.UPLOADED) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Attachment not found or not uploaded.");
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(s3Service.generatePresignedGetUrl(attachment.getS3Key()));
  }

  @StaffOnlyOrSelf
  @GetMapping("patients/{userId}")
  public ResponseEntity<List<FileInfoDTO>> getFilesForPatient(@PathVariable int userId) {
    List<Attachment> attachmentList = this.s3Service.getAttachmentsForPatient(userId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(s3Service.generateFileInfoDtosForAttachments(attachmentList));
  }

  @GetMapping("visits/{visitId}")
  public ResponseEntity<List<FileInfoDTO>> getFilesForVisit(@PathVariable int visitId) {
    Visit visit = visitService.getById(visitId);
    List<Attachment> attachmentList = this.s3Service.getAttachmentsForVisit(visit.getId());

    return ResponseEntity.status(HttpStatus.OK)
        .body(s3Service.generateFileInfoDtosForAttachments(attachmentList));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteFile(@PathVariable int id) {
    Attachment attachment = s3Service.getUploadedById(id);

    if (attachment == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attachment not found.");
    }
    s3Service.deleteFile(id);
    return ResponseEntity.status(HttpStatus.OK).body("Attachment deleted.");
  }
}
