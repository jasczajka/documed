package com.documed.backend.users.dtos;

import com.documed.backend.schedules.dtos.FreeDaysReturnDTO;
import com.documed.backend.users.model.Specialization;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Data
@Builder
public class DoctorDetailsDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Setter(AccessLevel.NONE)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<
          @NotNull(message = "Doctor needs to have at least one specialization") Specialization>
      specializations;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<FreeDaysReturnDTO> freeDays;
}
