package com.documed.backend.schedules;

import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.schedules.dtos.FreeDaysDTO;
import com.documed.backend.schedules.model.FreeDays;
import com.documed.backend.visits.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeDaysService {

    private final VisitService visitService;
    private final FreeDaysDAO freeDaysDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final TimeSlotService timeSlotService;

    @Transactional
    public void createFreeDay(FreeDaysDTO freeDaysDTO) {

        if (freeDaysDTO.getStartDate().isAfter(freeDaysDTO.getEndDate())){
            throw new BadRequestException("Start date cannot be after end date");
        }

        FreeDays freeDays = FreeDays
                .builder()
                .userId(freeDaysDTO.getUserId())
                .startDate(freeDaysDTO.getStartDate())
                .endDate(freeDaysDTO.getEndDate())
                .build();

        List<Integer> visitsToCancel = timeSlotService.getVisitIdsByDoctorAndDateRange(freeDays.getUserId(), freeDays.getStartDate(), freeDays.getEndDate());

        freeDaysDAO.create(freeDays);
        if (!visitsToCancel.isEmpty()) {
            visitsToCancel.forEach(visitService::cancelVisit);
        }
        timeSlotDAO.reserveTimeSlotsForFreeDays(freeDays);
    }

}
