package com.documed.backend.visits;

import com.documed.backend.schedules.TimeSlotService;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VisitService {

    private final VisitDAO visitDAO;
    private final TimeSlotService timeSlotService;

    Optional<Visit> getById(int id) {
        return visitDAO.getById(id);
    }

    public Visit scehduleVisit(ScheduleVisitDTO scheduleVisitDTO) {
        int timeSlotID = scheduleVisitDTO.getFirstTimeSlotId();
        TimeSlot timeSlot = timeSlotService.getTimeSlotById(timeSlotID).get();
        int serviceId = scheduleVisitDTO.getServiceId();


        return visitDAO.create(visit);
    }

    public Visit createVisit(ScheduleVisitDTO scheduleVisitDTO) {
        Visit visit = Visit.builder()
                .facilityId(scheduleVisitDTO.getFacilityId())
                .serviceId(scheduleVisitDTO.getServiceId())
                .doctorId()
        return visitDAO.create(scheduleVisitDTO);
    }

}
