package com.documed.backend.visits;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.exceptions.UnauthorizedException;
import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.others.EmailService;
import com.documed.backend.prescriptions.PrescriptionService;
import com.documed.backend.prescriptions.model.Prescription;
import com.documed.backend.referrals.ReferralService;
import com.documed.backend.referrals.model.Referral;
import com.documed.backend.schedules.TimeSlotService;
import com.documed.backend.schedules.model.TimeRange;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.services.ServiceService;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.SubscriptionService;
import com.documed.backend.users.services.UserService;
import com.documed.backend.visits.dtos.ScheduleVisitDTO;
import com.documed.backend.visits.dtos.UpdateVisitDTO;
import com.documed.backend.visits.exceptions.WrongVisitStatusException;
import com.documed.backend.visits.model.Feedback;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import com.documed.backend.visits.model.VisitWithDetails;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VisitService {

  private final VisitDAO visitDAO;
  private final FeedbackDAO feedbackDAO;
  private final TimeSlotService timeSlotService;
  private final AuthService authService;
  private final ServiceService serviceService;
  private final UserService userService;
  private final SubscriptionService subscriptionService;
  private final PrescriptionService prescriptionService;
  private final ReferralService referralService;
  private final EmailService emailService;

  public VisitWithDetails getByIdWithDetails(int id) {
    VisitWithDetails visit =
        visitDAO
            .findByIdWithDetails(id)
            .orElseThrow(() -> new NotFoundException("Visit not found"));

    if (authService.getCurrentUserRole() == UserRole.PATIENT
        && visit.getPatientId() != authService.getCurrentUserId()) {
      throw new UnauthorizedException("You are not authorized to access this resource");
    }

    return visit;
  }

  public List<VisitWithDetails> getAllWithDetailsBetweenDates(LocalDate startDate) {
    return visitDAO.findAllWithDetailsBetweenDates(startDate);
  }

  public List<VisitWithDetails> getVisitsForCurrentPatientWithDetailsBetweenDates(
      LocalDate startDate) {
    int patientId = authService.getCurrentUserId();
    int facilityId = authService.getCurrentFacilityId();
    return visitDAO.findByPatientIdAndFacilityIdWithDetailsBetweenDates(
        patientId, facilityId, startDate);
  }

  public List<VisitWithDetails> getVisitsByPatientIdWithDetailsBetweenDates(
      int patientId, LocalDate startDate) {
    int facilityId = authService.getCurrentFacilityId();
    return visitDAO.findByPatientIdAndFacilityIdWithDetailsBetweenDates(
        patientId, facilityId, startDate);
  }

  public List<VisitWithDetails> getVisitsByDoctorIdWithDetailsBetweenDates(
      int doctorId, LocalDate startDate) {
    int facilityId = authService.getCurrentFacilityId();
    return visitDAO.findByDoctorIdAndFacilityIdWithDetailsBetweenDates(
        doctorId, facilityId, startDate);
  }

  public List<VisitWithDetails> getVisitsForCurrentDoctorWithDetailsBetweenDates(
      LocalDate startDate) {
    int doctorId = authService.getCurrentUserId();
    int facilityId = authService.getCurrentFacilityId();
    return visitDAO.findByDoctorIdAndFacilityIdWithDetailsBetweenDates(
        doctorId, facilityId, startDate);
  }

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

    TimeRange timeRange = timeSlotService.reserveTimeSlotsForVisit(visit, timeSlot);

    visit.setDate(timeSlot.getDate());
    visit.setStartTime(timeRange.startTime());
    visit.setEndTime(timeRange.endTime());

    return visitDAO.updateWithTimeInfo(visit);
  }

  private Visit createVisit(ScheduleVisitDTO scheduleVisitDTO) {

    BigDecimal totalCost =
        calculateTotalCost(scheduleVisitDTO.getServiceId(), scheduleVisitDTO.getPatientId());

    Visit visit =
        Visit.builder()
            .facilityId(scheduleVisitDTO.getFacilityId())
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
    Visit visit =
        visitDAO.getById(visitId).orElseThrow(() -> new NotFoundException("Visit not found"));
    if (visit.getStatus() != VisitStatus.PLANNED) {
      throw new WrongVisitStatusException("Visit should be in status PLANNED");
    }
    if (authService.getCurrentUserId() != visit.getDoctorId()) {
      throw new UnauthorizedException("Only the doctor assigned to the visit can begin it");
    }
    return visitDAO.updateVisitStatus(visitId, VisitStatus.IN_PROGRESS);
  }

  @Transactional
  public boolean closeVisit(int visitId, UpdateVisitDTO updateVisitDTO) {
    if (visitDAO.getVisitStatus(visitId) != VisitStatus.IN_PROGRESS) {
      throw new WrongVisitStatusException("Visit should be in status IN PROGRESS");
    }
    removePrescriptionFromVisitIfEmpty(visitId);
    updateVisit(visitId, updateVisitDTO);
    List<Referral> referrals = referralService.getReferralsForVisit(visitId);
    Optional<Prescription> prescription = prescriptionService.getPrescriptionForVisit(visitId);
    prescription.ifPresent(value -> prescriptionService.issuePrescription(value.getId()));

    if (!referrals.isEmpty()) {
      referrals.forEach(referral -> referralService.issueReferral(referral.getId()));
    }
    return visitDAO.updateVisitStatus(visitId, VisitStatus.CLOSED);
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

  public BigDecimal calculateTotalCost(int serviceId, int patientId) {

    BigDecimal basicPrice = serviceService.getPriceForService(serviceId);
    Integer subscriptionId = userService.getSubscriptionIdForPatient(patientId);

    if (subscriptionId == null) {
      return basicPrice;
    } else {
      BigDecimal discount =
          BigDecimal.valueOf(
                  (100 - subscriptionService.getDiscountForService(serviceId, subscriptionId)))
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      if (discount.compareTo(BigDecimal.ZERO) > 0) {
        return basicPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
      } else {
        return basicPrice;
      }
    }
  }

  @Transactional
  public boolean cancelVisit(int visitId) {
    VisitWithDetails visit = getByIdWithDetails(visitId);
    int patientId = visit.getPatientId();
    if (authService.getCurrentUserRole() == UserRole.PATIENT
        && patientId != authService.getCurrentUserId()) {
      throw new UnauthorizedException("Patient can only cancel their own visit");
    }
    timeSlotService.releaseTimeSlotsForVisit(visitId);

    String email =
        userService
            .getById(patientId)
            .orElseThrow(() -> new NotFoundException("User not found"))
            .getEmail();

    emailService.sendCancelVisitEmail(email, visit.getDate());

    return visitDAO.updateVisitStatus(visitId, VisitStatus.CANCELLED);
  }

  private void removePrescriptionFromVisitIfEmpty(int visitId) {
    Optional<Integer> prescriptionId = prescriptionService.getPrescriptionIdForVisitId(visitId);
    if (prescriptionId.isPresent()
        && prescriptionService.getNumberOfMedicinesOnPrescriptionByVisitId(visitId) == 0) {
      prescriptionService.removePrescription(prescriptionId.get());
    }
  }

  public void giveFeedback(Feedback feedback) {

    if (feedback.getRating() < 1 || feedback.getRating() > 5) {
      throw new BadRequestException("Rating must be between 1 and 5");
    }

    Visit visit =
        visitDAO
            .getById(feedback.getVisitId())
            .orElseThrow(() -> new NotFoundException("Visit not found"));

    if (visit.getStatus() != VisitStatus.CLOSED) {
      throw new BadRequestException("Feedback can only be given for a closed visit");
    }

    if (feedbackDAO.getByVisitId(feedback.getVisitId()).isPresent()) {
      throw new BadRequestException("Feedback has already been given for this visit");
    }

    if (visit.getPatientId() != authService.getCurrentUserId()) {
      throw new UnauthorizedException("You can only give feedback for your visit");
    }

    feedbackDAO.create(feedback);
  }
}
