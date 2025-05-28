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
        .serviceId(service.getId())
        .serviceName(service.getName())
        .patientInformation(visit.getPatientInformation())
        .patientId(patient.getId())
        .patientFullName(patient.getFirstName() + " " + patient.getLastName())
        .patientBirthDate(patient.getBirthDate())
        .doctorId(doctor.getId())
        .doctorFullName(doctor.getFirstName() + " " + doctor.getLastName())
        .startTime(timeSlots.isEmpty() ? null : timeSlots.get(0).getStartTime())
        .endTime(timeSlots.isEmpty() ? null : timeSlots.get(timeSlots.size() - 1).getEndTime())
        .date(timeSlots.isEmpty() ? null : timeSlots.get(0).getDate())
        .build();
  }
}
