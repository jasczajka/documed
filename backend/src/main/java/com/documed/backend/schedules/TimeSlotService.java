package com.documed.backend.schedules;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.schedules.exceptions.NotEnoughTimeInTimeSlotException;
import com.documed.backend.schedules.model.TimeRange;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.services.ServiceService;
import com.documed.backend.visits.exceptions.CancelVisitException;
import com.documed.backend.visits.model.Visit;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TimeSlotService {

  @Value("${time-slot.duration-in-minutes}")
  private int slotDurationInMinutes;

  private final TimeSlotDAO timeSlotDAO;
  private final ServiceService serviceService;

  @Transactional
  public void createTimeSlotsForWorkTimes(List<WorkTime> workTimes) {
    for (WorkTime workTime : workTimes) {
      createTimeSlotForWorkTime(workTime);
    }
  }

  void createTimeSlotForWorkTime(WorkTime workTime) {
    long durationInMinutes =
        Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
    int requiredSlots = (int) durationInMinutes / slotDurationInMinutes;
    DayOfWeek dayOfWeek = workTime.getDayOfWeek();
    LocalDate date = LocalDate.now();
    LocalDate nextWeekDate = date.with(TemporalAdjusters.next(dayOfWeek));

    for (long i = 0; i < requiredSlots; i++) {
      TimeSlot timeSlot =
          TimeSlot.builder()
              .doctorId(workTime.getUserId())
              .startTime(workTime.getStartTime().plusMinutes(i * slotDurationInMinutes))
              .endTime(workTime.getStartTime().plusMinutes((i + 1) * slotDurationInMinutes))
              .date(nextWeekDate)
              .facilityId(workTime.getFacilityId())
              .build();
      timeSlotDAO.create(timeSlot);
    }
  }

  public Optional<TimeSlot> getTimeSlotById(int id) {
    return timeSlotDAO.getById(id);
  }

  @Transactional
  public TimeRange reserveTimeSlotsForVisit(Visit visit, TimeSlot firstTimeSlot) {
    int serviceId = visit.getServiceId();
    com.documed.backend.services.model.Service service =
        serviceService
            .getById(serviceId)
            .orElseThrow(() -> new NotFoundException("Service not found"));
    int neededTimeSlots =
        (int) Math.ceil((double) service.getEstimatedTime() / slotDurationInMinutes);

    List<TimeSlot> availableSlots =
        timeSlotDAO.getAvailableTimeSlotsByDoctorAndDate(
            firstTimeSlot.getDoctorId(), firstTimeSlot.getDate());

    availableSlots.sort(Comparator.comparing(TimeSlot::getId));

    int startIndex =
        IntStream.range(0, availableSlots.size())
            .filter(i -> availableSlots.get(i).getId() == firstTimeSlot.getId())
            .findFirst()
            .orElse(-1);

    if (startIndex == -1 || startIndex + neededTimeSlots > availableSlots.size()) {
      throw new NotEnoughTimeInTimeSlotException("Not enough continuous time slots available");
    }

    for (int i = 0; i < neededTimeSlots; i++) {

      TimeSlot slotToReserve = availableSlots.get(startIndex + i);

      if (i > 0 && !areTimeSlotsContinuous(availableSlots.get(startIndex + i - 1), slotToReserve)) {
        throw new NotEnoughTimeInTimeSlotException("Not enough continuous time slots available");
      }

      slotToReserve.setBusy(true);
      slotToReserve.setVisitId(visit.getId());

      timeSlotDAO.update(slotToReserve);
    }

    TimeSlot lastSlot = availableSlots.get(startIndex + neededTimeSlots - 1);
    return new TimeRange(firstTimeSlot.getStartTime(), lastSlot.getEndTime());
  }

  public List<TimeSlot> getAvailableFirstTimeSlotsByFacility(int neededTimeSlots, int facilityId) {
    List<TimeSlot> allSlots = timeSlotDAO.getAvailableFutureTimeSlotsByFacilityId(facilityId);

    Map<LocalDate, List<TimeSlot>> slotsByDate =
        allSlots.stream()
            .collect(Collectors.groupingBy(TimeSlot::getDate, TreeMap::new, Collectors.toList()));

    Set<TimeSlot> result = new LinkedHashSet<>();

    slotsByDate.forEach(
        (date, slotsForDate) -> {
          slotsForDate.sort(Comparator.comparing(TimeSlot::getStartTime));

          for (int i = 0; i <= slotsForDate.size() - neededTimeSlots; i++) {
            boolean isContinuous = true;

            for (int j = 0; j < neededTimeSlots - 1; j++) {
              if (!areTimeSlotsContinuous(slotsForDate.get(i + j), slotsForDate.get(i + j + 1))) {
                isContinuous = false;
                break;
              }
            }

            if (isContinuous) {
              result.add(slotsForDate.get(i));
            }
          }
        });

    return new ArrayList<>(result);
  }

  boolean areTimeSlotsContinuous(TimeSlot previousTimeSlot, TimeSlot currentTimeSlot) {
    return previousTimeSlot.getEndTime().equals(currentTimeSlot.getStartTime());
  }

  public void releaseTimeSlotsForVisit(int visitId) {
    if (!timeSlotDAO.releaseTimeSlotsForVisit(visitId)) {
      throw new CancelVisitException("Failed to release time slots for visit");
    }
  }

  public List<Integer> getVisitIdsByDoctorAndDateRange(
      int doctorId, LocalDate fromDate, LocalDate toDate) {
    return timeSlotDAO.getVisitIdsByDoctorAndDateRange(doctorId, fromDate, toDate);
  }
}
