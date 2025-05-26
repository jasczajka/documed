package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

public class TimeSlotMapper {

  public static AvailableTimeSlotDTO toDto(TimeSlot timeSlot) {
    if (timeSlot == null) {
      return null;
    }

    return AvailableTimeSlotDTO.builder()
        .id(timeSlot.getId())
        .doctorId(timeSlot.getDoctorId())
        .startTime(convertToDate(timeSlot.getDate(), timeSlot.getStartTime()))
        .isBusy(timeSlot.isBusy())
        .build();
  }

  private static Date convertToDate(LocalDate date, LocalTime time) {
    return Date.from(LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC));
  }
}
