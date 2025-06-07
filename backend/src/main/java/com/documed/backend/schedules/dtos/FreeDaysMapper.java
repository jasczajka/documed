package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.FreeDays;

public class FreeDaysMapper {

  public static FreeDaysReturnDTO toDTO(FreeDays freeDays) {
    return FreeDaysReturnDTO.builder()
        .id(freeDays.getId())
        .startDate(freeDays.getStartDate())
        .endDate(freeDays.getEndDate())
        .userId(freeDays.getUserId())
        .build();
  }
}
