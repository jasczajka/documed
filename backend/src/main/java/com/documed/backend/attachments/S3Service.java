package com.documed.backend.attachments;

import com.documed.backend.attachments.exceptions.FileUploadFailedException;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.exceptions.NotFoundException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Presigner s3Presigner;
  private final S3Config s3Config;
  private final AttachmentDAO attachmentDAO;

  private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 50MB in bytes

  @Value("${aws.s3.bucket_name}")
  private String bucketName;

  public Attachment createAttachment(Attachment attachment) {
    return attachmentDAO.create(attachment);
  }

  public String generatePreSignedPutUrl(String objectKey, long fileSizeBytes) {

    if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
      throw new FileUploadFailedException("File size exceeds 50MB limit");
    }
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .contentLength(fileSizeBytes)
            .build();

    PresignedPutObjectRequest presignedRequest =
        s3Presigner.presignPutObject(
            builder ->
                builder
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(Duration.ofMinutes(15)));

    return presignedRequest.url().toString();
  }

  public Attachment getUploadedById(int id) {
    return attachmentDAO
        .getUploadedById(id)
        .orElseThrow(() -> new FileUploadFailedException("Attachment not found"));
  }

  public void completeFileUpload(int id) {
    Attachment attachment =
        attachmentDAO
            .getById(id)
            .orElseThrow(() -> new FileUploadFailedException("Attachment not found"));

    try {
      HeadObjectResponse headResponse =
          s3Config
              .s3Client()
              .headObject(
                  HeadObjectRequest.builder()
                      .bucket(bucketName)
                      .key(attachment.getS3Key())
                      .build());

      if (headResponse.sdkHttpResponse() == null
          || !headResponse.sdkHttpResponse().isSuccessful()) {
        throw new FileUploadFailedException("S3 HEAD operation failed");
      }

      boolean result = this.attachmentDAO.setAttachmentAsUploaded(id);
      if (!result) {
        throw new FileUploadFailedException("Failed to update upload status");
      }

    } catch (NoSuchKeyException e) {
      throw new FileUploadFailedException("File not found in S3");
    } catch (S3Exception e) {
      throw new FileUploadFailedException("S3 error: " + e.awsErrorDetails().errorMessage());
    }
  }

  public String generatePresignedGetUrl(String key) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(key).build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(1))
            .getObjectRequest(getObjectRequest)
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  public List<String> generatePresignedGetUrlsForVisit(int visitId) {
    ArrayList<String> urls = new ArrayList<>();
    List<Attachment> attachments = this.attachmentDAO.getUploadedByVisitId(visitId);
    attachments.forEach(
        attachment -> {
          String url = this.generatePresignedGetUrl(attachment.getS3Key());
          urls.add(url);
        });
    return urls;
  }

  public List<String> generatePresignedGetUrlsForAdditionalService(int additionalServiceId) {
    ArrayList<String> urls = new ArrayList<>();
    List<Attachment> attachments =
        this.attachmentDAO.getUploadedByAdditionalServiceId(additionalServiceId);
    attachments.forEach(
        attachment -> {
          String url = this.generatePresignedGetUrl(attachment.getS3Key());
          urls.add(url);
        });
    return urls;
  }

  public List<Attachment> getAttachmentsForAdditionalService(int additionalServiceId) {
    return this.attachmentDAO.getUploadedByAdditionalServiceId(additionalServiceId);
  }

  public List<Attachment> getAttachmentsForVisit(int visitId) {
    return this.attachmentDAO.getUploadedByVisitId(visitId);
  }

  public void deleteFile(int fileId) {
    Attachment attachmentToDelete =
        attachmentDAO
            .getById(fileId)
            .orElseThrow(() -> new NotFoundException("Attachment not found"));
    attachmentDAO.delete(fileId);
    s3Config
        .s3Client()
        .deleteObject(
            DeleteObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(attachmentToDelete.getS3Key())
                .build());
  }
}
