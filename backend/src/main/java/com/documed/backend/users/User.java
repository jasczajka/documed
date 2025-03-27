package com.documed.backend.users;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.schedules.FreeDay;
import com.documed.backend.schedules.TimeSlot;
import com.documed.backend.schedules.WorkTime;
import com.documed.backend.visits.Visit;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class User {

  private int id;
  @NonNull private String firstName;
  @NonNull private String lastName;
  private String pesel;
  private String passportNumber;
  @NonNull private String email;
  @NonNull private String address;
  @NonNull private String password;
  private String phoneNumber;
  @NonNull private String status;
  private Date birthDate;
  private String pwzNumber;
  @NonNull private UserRole role;
  private Subscription subscription;
  private List<Specialization> specializations;
  private List<TimeSlot> timeSlots;
  private List<WorkTime> workTimes;
  private List<FreeDay> freeDays;
  private List<Visit> visits;
  private List<AdditionalService> additionalServices;
}
