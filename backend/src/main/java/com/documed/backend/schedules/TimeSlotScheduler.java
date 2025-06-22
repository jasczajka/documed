package com.documed.backend.schedules;

import com.documed.backend.schedules.model.WorkTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TimeSlotScheduler {

  private final TimeSlotService timeSlotService;
  private final WorkTimeService workTimeService;

  // every Monday at midnight
  @Scheduled(cron = "0 0 0 * * 1")
  public void scheduleTimeSlots() {
    List<WorkTime> workTimes = workTimeService.getAllWorkTimes();
    timeSlotService.createTimeSlotsForWorkTimes(workTimes);
  }
}
