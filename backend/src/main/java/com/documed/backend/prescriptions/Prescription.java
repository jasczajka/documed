package com.documed.backend.prescriptions;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

// @TODO prescription return DTO, needs to include issuing doctor
//  https://dokumentacjamedyczna.atlassian.net/browse/MED-109

@Data
@Builder
public class Prescription {
  @Schema(required = true)
  private int id;

  @Schema(required = true)
  private int accessCode;

  @Schema(required = true)
  private final Date date;

  @Schema(required = true)
  private Date expirationDate;

  @Schema(required = true)
  private PrescriptionStatus status;
}
