package com.documed.backend.attachments.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {
  private int id;
  private String fileName;
  private String s3Key;
  AttachmentStatus status;
  private Integer visitId;
  private Integer additionalServiceId;
}
