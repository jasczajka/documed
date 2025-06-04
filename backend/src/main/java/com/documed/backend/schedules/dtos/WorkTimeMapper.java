package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.DayOfWeekEnum;
import com.documed.backend.schedules.model.WorkTime;

public class WorkTimeMapper {

  public static WorkTimeReturnDTO toDto(WorkTime workTime) {
    if (workTime == null) {
      return null;
    }

    return WorkTimeReturnDTO.builder()
        .id(workTime.getId())
        .userId(workTime.getUserId())
        .facilityId(workTime.getFacilityId())
        .dayOfWeek(DayOfWeekEnum.fromJavaDayOfWeek(workTime.getDayOfWeek()))
        .startTime(workTime.getStartTime())
        .endTime(workTime.getEndTime())
        .build();
  }
}
