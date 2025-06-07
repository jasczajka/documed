package com.documed.backend.schedules;

import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.schedules.dtos.FreeDaysDTO;
import com.documed.backend.schedules.model.FreeDays;
import com.documed.backend.visits.VisitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FreeDaysService {

  private final VisitService visitService;
  private final FreeDaysDAO freeDaysDAO;
  private final TimeSlotDAO timeSlotDAO;
  private final TimeSlotService timeSlotService;

  @Transactional
  public FreeDays createFreeDays(FreeDaysDTO freeDaysDTO) {

    if (freeDaysDTO.getStartDate().isAfter(freeDaysDTO.getEndDate())) {
      throw new BadRequestException("Start date cannot be after end date");
    }

    FreeDays freeDays =
        FreeDays.builder()
            .userId(freeDaysDTO.getUserId())
            .startDate(freeDaysDTO.getStartDate())
            .endDate(freeDaysDTO.getEndDate())
            .build();

    List<Integer> visitsToCancel =
        timeSlotService.getVisitIdsByDoctorAndDateRange(
            freeDays.getUserId(), freeDays.getStartDate(), freeDays.getEndDate());

    List<FreeDays> existingFreeDays = getFreeDaysByUserId(freeDays.getUserId());
    boolean hasOverlap =
        existingFreeDays.stream()
            .anyMatch(
                existingFreeDay ->
                    !(existingFreeDay.getEndDate().isBefore(freeDaysDTO.getStartDate())
                        || existingFreeDay.getStartDate().isAfter(freeDaysDTO.getEndDate())));
    if (hasOverlap) {
      throw new BadRequestException("Free days overlap with existing entries");
    }

    FreeDays created = freeDaysDAO.create(freeDays);
    if (!visitsToCancel.isEmpty()) {
      visitsToCancel.forEach(visitService::cancelVisit);
    }
    timeSlotDAO.reserveTimeSlotsForFreeDays(freeDays);
    return created;
  }

  @Transactional
  public void cancelFreeDays(int freeDaysId) {

    FreeDays freeDays =
        freeDaysDAO
            .getById(freeDaysId)
            .orElseThrow(() -> new NotFoundException("FreeDays not found"));

    timeSlotDAO.releaseTimeSlotsForFreeDays(freeDays);
    freeDaysDAO.delete(freeDaysId);
  }

  public List<FreeDays> getFreeDaysByUserId(int userId) {
    return freeDaysDAO.getByUserId(userId);
  }
}
