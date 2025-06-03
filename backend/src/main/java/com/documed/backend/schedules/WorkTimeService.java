package com.documed.backend.schedules;

import com.documed.backend.auth.AuthService;
import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.schedules.dtos.WorkTimeDTO;
import com.documed.backend.schedules.exceptions.WrongTimesGivenException;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
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
  private final AuthService authService;
  private final UserService userService;

  WorkTime createWorkTime(int userId, WorkTimeDTO dto) {

    if (!userService.isUserAssignedToRole(userId, UserRole.DOCTOR)) {
      throw new BadRequestException("User is not a doctor");
    }

    int facilityId = authService.getCurrentFacilityId();

    WorkTime workTime =
        WorkTime.builder()
            .userId(userId)
            .dayOfWeek(dto.getDayOfWeek())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .facilityId(facilityId)
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

  // TODO will be invoked when creating new employee
  List<WorkTime> createWorkTimeForNewUser(int userId, UserRole role) {

    if (role == UserRole.DOCTOR) {
      List<WorkTime> workTimes = new ArrayList<>();
      for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
        workTimes.add(WorkTime.builder().userId(userId).dayOfWeek(dayOfWeek).build());
        workTimeDAO.create(workTimes.getLast());
      }
      return workTimes;
    } else {
      return new ArrayList<>();
    }
  }

  List<WorkTime> getWorkTimesForUser(int userId) {
    return workTimeDAO.getWorkTimesForUser(userId);
  }

  @Transactional
  public List<WorkTime> updateWorkTimes(List<WorkTimeDTO> updatedWorkTimes, int userId) {

    if (!userService.isUserAssignedToRole(userId, UserRole.DOCTOR)) {
      throw new BadRequestException("User is not a doctor");
    }

    int facilityId = authService.getCurrentFacilityId();

    List<WorkTime> workTimes = new ArrayList<>();
    for (WorkTimeDTO workTime : updatedWorkTimes) {

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
                .dayOfWeek(workTime.getDayOfWeek())
                .startTime(workTime.getStartTime())
                .endTime(workTime.getEndTime())
                .facilityId(facilityId)
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
