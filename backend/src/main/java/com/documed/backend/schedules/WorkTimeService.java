package com.documed.backend.schedules;

import com.documed.backend.schedules.model.WorkTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkTimeService {

    private final WorkTimeDAO workTimeDAO;

    WorkTime createWorkTime(WorkTime workTime) {
        return workTimeDAO.create(workTime);
    }

}
