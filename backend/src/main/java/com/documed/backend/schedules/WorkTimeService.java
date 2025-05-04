package com.documed.backend.schedules;

import com.documed.backend.schedules.model.WorkTime;
import com.documed.backend.users.model.UserRole;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkTimeService {

  private final WorkTimeDAO workTimeDAO;

  WorkTime createWorkTime(WorkTime workTime) {
    return workTimeDAO.create(workTime);
  }

  // TODO will be invoked when creating new employeeTim
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

  List<WorkTime> updateWorkTimes(List<WorkTimeDTO> updatedWorkTimes, int userId) {
    List<WorkTime> workTimes = new ArrayList<>();
    for (WorkTimeDTO workTime : updatedWorkTimes) {
      workTimes.add(
          WorkTime.builder()
              .userId(userId)
              .dayOfWeek(workTime.getDayOfWeek())
              .startTime(workTime.getStartTime())
              .endTime(workTime.getEndTime())
              .build());
      workTimeDAO.updateWorkTime(workTimes.getLast());
    }
    return workTimes;
  }

  List<WorkTime> getAllWorkTimes() {
    return workTimeDAO.getAll();
  }
}
