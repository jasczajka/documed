package com.documed.backend.services.model;

import com.documed.backend.services.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Data
@Builder
public class Service {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private BigDecimal price;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private ServiceType type;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int estimatedTime;

  @NotEmpty(message = "Musisz podać przynajmniej jedną specjalizację") @NotNull(message = "ID specjalizacji nie może być puste") @Positive(message = "ID specjalizacji musi być większe od zera") private List<Integer> specializationIds;
}
