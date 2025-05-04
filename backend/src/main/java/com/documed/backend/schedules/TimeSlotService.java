package com.documed.backend.schedules;

import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.visits.Visit;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotService {

  private final TimeSlotDAO timeSlotDAO;

  public TimeSlotService(TimeSlotDAO timeSlotDAO) {
    this.timeSlotDAO = timeSlotDAO;
  }

  TimeSlot createTimeSlot(CreateTimeSlotDTO timeSlotDTO) {
    TimeSlot timeSlot =
        TimeSlot.builder()
            .doctorId(timeSlotDTO.getDoctorId())
            .startTime(timeSlotDTO.getStartTime())
            .endTime(timeSlotDTO.getEndTime())
            .isBusy(false)
            .date(timeSlotDTO.getDate())
            .build();
    return timeSlotDAO.create(timeSlot);
  }

  void createTimeSlotsForWorkTimes(List<WorkTime> workTimes) {
    for (WorkTime workTime : workTimes) {
      createTimeSlotForWorkTime(workTime);
    }
  }

  void createTimeSlotForWorkTime(WorkTime workTime) {
    long durationInMinutes =
        Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
    int requiredSlots = (int) durationInMinutes / 15;
    DayOfWeek dayOfWeek = workTime.getDayOfWeek();
    LocalDate date = LocalDate.now();
    LocalDate nextWeekDate = date.with(TemporalAdjusters.next(dayOfWeek));

    for (long i = 0; i < requiredSlots; i++) {
      TimeSlot timeSlot =
          TimeSlot.builder()
              .doctorId(workTime.getUserId())
              .startTime(workTime.getStartTime().plusMinutes(i * 15))
              .endTime(workTime.getStartTime().plusMinutes((i + 1) * 15))
              .date(nextWeekDate)
              .build();
      timeSlotDAO.create(timeSlot);
    }
  }

  Optional<TimeSlot> getTimeSlotById(int id) {
    return timeSlotDAO.getById(id);
  }

  void reserveTimeSlotsForVisit(Visit visit, TimeSlot firstTimeSlot) {
    int neededTimeSlots = visit.getService().getEstimatedTime() / 15;

    List<TimeSlot> availableSlots =
        timeSlotDAO.getAvailableTimeSlotsByDoctorAndDate(
            firstTimeSlot.getDoctorId(), firstTimeSlot.getDate());

    int startIndex = availableSlots.indexOf(firstTimeSlot);

    if (startIndex == -1 || startIndex + neededTimeSlots > availableSlots.size()) {
      throw new RuntimeException("Not enough continuous time slots available");
    }

    for (int i = 0; i < neededTimeSlots; i++) {
      TimeSlot slotToReserve = availableSlots.get(startIndex + i);

      slotToReserve.setBusy(true);
      slotToReserve.setVisitId(visit.getId());

      timeSlotDAO.update(slotToReserve);
    }
  }
}
