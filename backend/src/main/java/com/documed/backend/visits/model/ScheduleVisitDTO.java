package com.documed.backend.visits.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleVisitDTO {
    private final String patientInformation;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID pacjenta jest wymagane") private final int patientId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID slotu jest wymagane") private final int firstTimeSlotId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID usługi jest wymagane") private final int serviceId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID placówki jest wymagane") private final int facilityId;

}
