package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlotMapper {

  public static AvailableTimeSlotDTO toDto(TimeSlot timeSlot) {
    if (timeSlot == null) {
      return null;
    }

    return AvailableTimeSlotDTO.builder()
        .id(timeSlot.getId())
        .doctorId(timeSlot.getDoctorId())
        .startTime(convertToLocalDateTime(timeSlot.getDate(), timeSlot.getStartTime()))
        .isBusy(timeSlot.isBusy())
        .build();
  }

  private static LocalDateTime convertToLocalDateTime(LocalDate date, LocalTime time) {
    return LocalDateTime.of(date, time);
  }
}
