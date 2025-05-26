package com.documed.backend.visits;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.exceptions.UnauthorizedException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.schedules.TimeSlotService;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.services.ServiceService;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.visits.dtos.UpdateVisitDTO;
import com.documed.backend.visits.exceptions.WrongVisitStatusException;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VisitService {

  private final VisitDAO visitDAO;
  private final TimeSlotService timeSlotService;
  private final AuthService authService;
  private final ServiceService serviceService;

  public Visit getById(int id) {
    Visit visit = visitDAO.getById(id).orElseThrow(() -> new NotFoundException("Visit not found"));
    if (authService.getCurrentUserRole() == UserRole.PATIENT
        && visit.getPatientId() != authService.getCurrentUserId()) {
      throw new UnauthorizedException("You are not authorized to access this resource");
    }

    return visit;
  }

  @Transactional
  public Visit scheduleVisit(ScheduleVisitDTO scheduleVisitDTO) {

    TimeSlot timeSlot =
        timeSlotService
            .getTimeSlotById(scheduleVisitDTO.getFirstTimeSlotId())
            .orElseThrow(() -> new NotFoundException("Could not retrieve slot with given ID"));

    Visit visit = createVisit(scheduleVisitDTO);

    timeSlotService.reserveTimeSlotsForVisit(visit, timeSlot);

    return visit;
  }

  private Visit createVisit(ScheduleVisitDTO scheduleVisitDTO) {

    BigDecimal totalCost =
        calculateTotalCost(scheduleVisitDTO.getServiceId(), scheduleVisitDTO.getPatientId());

    Visit visit =
        Visit.builder()
            .facilityId(authService.getCurrentFacilityId())
            .serviceId(scheduleVisitDTO.getServiceId())
            .patientId(scheduleVisitDTO.getPatientId())
            .doctorId(scheduleVisitDTO.getDoctorId())
            .totalCost(totalCost)
            .status(VisitStatus.PLANNED)
            .patientInformation(scheduleVisitDTO.getPatientInformation())
            .build();
    return visitDAO.create(visit);
  }

  boolean startVisit(int visitId) {
    if (visitDAO.getVisitStatus(visitId) != VisitStatus.PLANNED) {
      throw new WrongVisitStatusException("Visit should be in status PLANNED");
    }
    return visitDAO.updateVisitStatus(visitId, VisitStatus.IN_PROGRESS);
  }

  @Transactional
  public boolean closeVisit(int visitId, UpdateVisitDTO updateVisitDTO) {
    if (visitDAO.getVisitStatus(visitId) != VisitStatus.IN_PROGRESS) {
      throw new WrongVisitStatusException("Visit should be in status IN PROGRESS");
    }

    updateVisit(visitId, updateVisitDTO);
    return visitDAO.updateVisitStatus(visitId, VisitStatus.CLOSED);
  }

  List<Visit> getVisitsForCurrentPatient() {
    return visitDAO.getVisitsByPatientIdAndFacilityId(
        authService.getCurrentUserId(), authService.getCurrentFacilityId());
  }

  List<Visit> getVisitsByPatientId(int patientId) {
    return visitDAO.getVisitsByPatientIdAndFacilityId(
        patientId, authService.getCurrentFacilityId());
  }

  List<Visit> getVisitsByDoctorId(int doctorId) {
    return visitDAO.getVisitsByDoctorIdAndFacilityId(doctorId, authService.getCurrentFacilityId());
  }

  List<Visit> getVisitsForCurrentDoctor() {
    return visitDAO.getVisitsByDoctorIdAndFacilityId(
        authService.getCurrentUserId(), authService.getCurrentFacilityId());
  }

  Visit updateVisit(int visitId, UpdateVisitDTO updateVisitDTO) {
    Visit visit =
        visitDAO
            .getById(visitId)
            .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

    visit.setInterview(updateVisitDTO.getInterview());
    visit.setDiagnosis(updateVisitDTO.getDiagnosis());
    visit.setRecommendations(updateVisitDTO.getRecommendations());

    return visitDAO.update(visit);
  }

  BigDecimal calculateTotalCost(int serviceId, int patientId) {

    BigDecimal basicPrice = serviceService.getPriceForService(serviceId);
    // @TODO: calculate total cost based on subscription
    return basicPrice;
  }

  @Transactional
  public boolean cancelVisit(int visitId) {

    timeSlotService.releaseTimeSlotsForVisit(visitId);
    return visitDAO.updateVisitStatus(visitId, VisitStatus.CANCELLED);
  }
}
