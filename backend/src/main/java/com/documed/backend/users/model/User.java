package com.documed.backend.users.model;

import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.schedules.model.FreeDay;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.visits.Visit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import lombok.*;

@Data
@Builder
public class User {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Setter(AccessLevel.NONE)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String lastName;

  @Schema private String pesel;

  @Schema private String passportNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String email;

  @Schema private String address;

  @Schema @ToString.Exclude private String password;

  @Schema private String phoneNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private AccountStatus accountStatus;

  @Schema private Date birthDate;

  @Schema private String pwzNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private boolean emailNotifications;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private UserRole role;

  @Schema private Subscription subscription;

  @Schema private List<Specialization> specializations;

  @Schema private List<TimeSlot> timeSlots;

  @Schema private List<WorkTime> workTimes;

  @Schema private List<FreeDay> freeDays;

  @Schema private List<Visit> visits;

  @Schema private List<AdditionalService> additionalServices;
}
