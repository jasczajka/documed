package com.documed.backend.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDoctorSpecializationsDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotEmpty(message = "Musisz podać przynajmniej jedną specjalizację") private List<@NotNull(message = "ID specjalizacji nie może być puste") Integer> specializationIds;
}
