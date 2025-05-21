package com.documed.backend.attachments.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Attachment {
  @NonNull private int id;
  @NonNull private String fileName;
  @NonNull private String s3Key;
  @NonNull AttachmentStatus status;
  private Integer visitId;
  private Integer additionalServiceId;
}
