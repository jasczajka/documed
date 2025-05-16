package com.documed.backend.visits;

import com.documed.backend.auth.AuthService;
import com.documed.backend.schedules.TimeSlotService;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VisitService {

  private final VisitDAO visitDAO;
  private final TimeSlotService timeSlotService;
  private final AuthService authService;

  public Optional<Visit> getById(int id) {
    return visitDAO.getById(id);
  }

  @Transactional
  public Visit scheduleVisit(ScheduleVisitDTO scheduleVisitDTO) {

    TimeSlot timeSlot =
        timeSlotService
            .getTimeSlotById(scheduleVisitDTO.getFirstTimeSlotId())
            .orElseThrow(
                () -> new IllegalArgumentException("Could not retrieve slot with given ID"));

    Visit visit = createVisit(scheduleVisitDTO);

    timeSlotService.reserveTimeSlotsForVisit(visit, timeSlot);

    return visit;
  }

  public Visit createVisit(ScheduleVisitDTO scheduleVisitDTO) {

    Visit visit =
        Visit.builder()
            .facilityId(authService.getCurrentFacilityId())
            .serviceId(scheduleVisitDTO.getServiceId())
            .patientId(scheduleVisitDTO.getPatientId())
            .status(VisitStatus.PLANNED)
            .build();
    return visitDAO.create(visit);
  }

  boolean startVisit(int visitId) {
    return visitDAO.updateVisitStatus(visitId, VisitStatus.IN_PROGRESS);
  }

  List<Visit> getVisitsForCurrentPatient() {
    return visitDAO.getVisitsByPatientId(authService.getCurrentUserId());
  }

  List<Visit> getVisitsByPatientId(int patientId) {
    return visitDAO.getVisitsByPatientId(patientId);
  }

  List<Visit> getVisitsByDoctorId(int doctorId) {
    return visitDAO.getVisitsByDoctorId(doctorId);
  }

  List<Visit> getVisitsForCurrentDoctor() {
    return visitDAO.getVisitsByDoctorId(authService.getCurrentUserId());
  }
}
