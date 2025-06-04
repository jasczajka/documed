package com.documed.backend.schedules;

import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.schedules.dtos.UploadWorkTimeDTO;
import com.documed.backend.schedules.exceptions.WrongTimesGivenException;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkTimeService {

  @Value("${time-slot.duration-in-minutes}")
  private int slotDurationInMinutes;

  private final WorkTimeDAO workTimeDAO;
  private final UserService userService;

  WorkTime createWorkTime(int userId, UploadWorkTimeDTO dto) {

    if (!userService.isUserAssignedToRole(userId, UserRole.DOCTOR)) {
      throw new BadRequestException("User is not a doctor");
    }

    WorkTime workTime =
        WorkTime.builder()
            .userId(userId)
            .dayOfWeek(dto.getDayOfWeek().toJavaDayOfWeek())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .facilityId(dto.getFacilityId())
            .build();

    long duration = Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
    if (duration < slotDurationInMinutes && duration != 0) {
      throw new WrongTimesGivenException(
          "Różnica pomiędzy godzinami nie może być krótsza niż slot czasowy.");
    } else if (!workTime.getStartTime().isBefore(workTime.getEndTime()) && duration != 0) {
      throw new WrongTimesGivenException(
          "Czas rozpoczęcia musi być wcześniejszy niż czas zakończenia.");
    } else {
      return workTimeDAO.create(workTime);
    }
  }

  public List<WorkTime> createWorkTimeForNewDoctor(int userId, int facilityId) {
    return Arrays.stream(DayOfWeek.values())
        .map(
            day -> {
              WorkTime workTime =
                  WorkTime.builder()
                      .userId(userId)
                      .facilityId(facilityId)
                      .dayOfWeek(day)
                      .startTime(LocalTime.MIDNIGHT)
                      .endTime(LocalTime.MIDNIGHT)
                      .build();
              workTimeDAO.create(workTime);
              return workTime;
            })
        .toList();
  }

  List<WorkTime> getWorkTimesForUser(int userId) {
    return workTimeDAO.getWorkTimesForUser(userId);
  }

  @Transactional
  public List<WorkTime> updateWorkTimes(List<UploadWorkTimeDTO> updatedWorkTimes, int userId) {

    if (!userService.isUserAssignedToRole(userId, UserRole.DOCTOR)) {
      throw new BadRequestException("User is not a doctor");
    }

    List<WorkTime> workTimes = new ArrayList<>();
    for (UploadWorkTimeDTO workTime : updatedWorkTimes) {

      long duration = Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
      if (duration < slotDurationInMinutes && duration != 0) {
        throw new WrongTimesGivenException(
            "Różnica pomiędzy godzinami nie może być krótsza niż slot czasowy.");
      } else if (!workTime.getStartTime().isBefore(workTime.getEndTime()) && duration != 0) {
        throw new WrongTimesGivenException(
            "Czas rozpoczęcia musi być wcześniejszy niż czas zakończenia.");
      } else {
        workTimes.add(
            WorkTime.builder()
                .userId(userId)
                .dayOfWeek(workTime.getDayOfWeek().toJavaDayOfWeek())
                .startTime(workTime.getStartTime())
                .endTime(workTime.getEndTime())
                .facilityId(workTime.getFacilityId())
                .build());
        workTimeDAO.updateWorkTime(workTimes.getLast());
      }
    }
    return workTimes;
  }

  List<WorkTime> getAllWorkTimes() {
    return workTimeDAO.getAll();
  }
}
