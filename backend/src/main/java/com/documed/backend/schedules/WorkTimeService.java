package com.documed.backend.schedules;

import com.documed.backend.schedules.exceptions.WrongTimesGivenException;
import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.schedules.model.WorkTimeDTO;
import com.documed.backend.users.model.UserRole;
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

  WorkTime createWorkTime(WorkTime workTime) {
    long duration = Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
    if (duration < slotDurationInMinutes) {
      throw new WrongTimesGivenException(
          "Różnica pomiędzy godzinami nie może być krótsza niż slot czasowy.");
    } else if (!workTime.getStartTime().isBefore(workTime.getEndTime())) {
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
    List<WorkTime> workTimes = new ArrayList<>();
    for (WorkTimeDTO workTime : updatedWorkTimes) {

      long duration = Duration.between(workTime.getStartTime(), workTime.getEndTime()).toMinutes();
      if (duration < slotDurationInMinutes) {
        throw new WrongTimesGivenException(
            "Różnica pomiędzy godzinami nie może być krótsza niż slot czasowy.");
      } else if (!workTime.getStartTime().isBefore(workTime.getEndTime())) {
        throw new WrongTimesGivenException(
            "Czas rozpoczęcia musi być wcześniejszy niż czas zakończenia.");
      } else {
        workTimes.add(
            WorkTime.builder()
                .userId(userId)
                .dayOfWeek(workTime.getDayOfWeek())
                .startTime(workTime.getStartTime())
                .endTime(workTime.getEndTime())
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
