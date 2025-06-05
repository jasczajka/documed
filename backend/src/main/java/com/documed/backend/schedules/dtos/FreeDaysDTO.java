package com.documed.backend.schedules.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FreeDaysDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    int userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @FutureOrPresent
    LocalDate startDate;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @FutureOrPresent
    LocalDate endDate;
}
