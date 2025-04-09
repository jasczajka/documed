package com.documed.backend.users;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.schedules.FreeDay;
import com.documed.backend.schedules.TimeSlot;
import com.documed.backend.schedules.WorkTime;
import com.documed.backend.visits.Visit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import lombok.*;

@Data
@Builder
public class User {

  @Schema(required = true)
  @Setter(AccessLevel.NONE)
  private int id;

  @Schema(required = true)
  @NonNull private String firstName;

  @Schema(required = true)
  @NonNull private String lastName;

  @Schema private String pesel;

  @Schema private String passportNumber;

  @Schema(required = true)
  @NonNull private String email;

  @Schema(required = true)
  @NonNull private String address;

  @Schema @ToString.Exclude private String password;

  @Schema private String phoneNumber;

  @Schema(required = true)
  @NonNull private AccountStatus accountStatus;

  @Schema private Date birthDate;

  @Schema private String pwzNumber;

  @Schema(required = true)
  @NonNull private UserRole role;

  @Schema private Subscription subscription;

  @Schema private List<Specialization> specializations;

  @Schema private List<TimeSlot> timeSlots;

  @Schema private List<WorkTime> workTimes;

  @Schema private List<FreeDay> freeDays;

  @Schema private List<Visit> visits;

  @Schema private List<AdditionalService> additionalServices;
}
