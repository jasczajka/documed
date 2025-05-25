package com.documed.backend.visits.dtos;

import com.documed.backend.visits.model.Visit;

public class VisitMapper {

  public static VisitDTO toDto(Visit visit) {
    if (visit == null) {
      return null;
    }

    return VisitDTO.builder()
        .id(visit.getId())
        .status(visit.getStatus())
        .interview(visit.getInterview())
        .diagnosis(visit.getDiagnosis())
        .recommendations(visit.getRecommendations())
        .totalCost(visit.getTotalCost())
        .facilityId(visit.getFacilityId())
        .serviceId(visit.getServiceId())
        .patientInformation(visit.getPatientInformation())
        .patientId(visit.getPatientId())
        .build();
  }
}
