package com.documed.backend.visits.dtos;

import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.model.User;
import com.documed.backend.visits.model.Visit;
import java.util.List;

public class VisitMapper {

  public static VisitDTO toDto(
      Visit visit, User patient, User doctor, Service service, List<TimeSlot> timeSlots) {
    return VisitDTO.builder()
        .id(visit.getId())
        .status(visit.getStatus())
        .interview(visit.getInterview())
        .diagnosis(visit.getDiagnosis())
        .recommendations(visit.getRecommendations())
        .totalCost(visit.getTotalCost())
        .facilityId(visit.getFacilityId())
        .serviceName(service.getName())
        .serviceId(service.getId())
        .patientInformation(visit.getPatientInformation())
        .patientFullName(patient.getFirstName() + " " + patient.getLastName())
        .patientId(patient.getId())
        .doctorFullName(doctor.getFirstName() + " " + doctor.getLastName())
        .doctorId(doctor.getId())
        .startTime(timeSlots.getFirst().getStartTime())
        .endTime(timeSlots.getLast().getEndTime())
        .date(timeSlots.getFirst().getDate())
        .build();
  }
}
