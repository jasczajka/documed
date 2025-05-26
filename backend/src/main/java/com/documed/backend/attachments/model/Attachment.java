package com.documed.backend.attachments.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Attachment {
  private int id;
  private String fileName;
  private String s3Key;
  @NonNull AttachmentStatus status;
  private Integer visitId;
  private Integer additionalServiceId;
}
