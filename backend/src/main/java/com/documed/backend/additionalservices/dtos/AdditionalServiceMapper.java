package com.documed.backend.additionalservices.dtos;

import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.model.User;

public class AdditionalServiceMapper {

  public static AdditionalServiceReturnDTO toDto(
      AdditionalService additionalService, User patient, User fulfiller, Service service) {
    if (additionalService == null) {
      return null;
    }

    return AdditionalServiceReturnDTO.builder()
        .id(additionalService.getId())
        .description(additionalService.getDescription())
        .date(additionalService.getDate())
        .fulfillerId(fulfiller.getId())
        .fulfillerFullName(fulfiller.getFirstName() + " " + fulfiller.getLastName())
        .patientId(patient.getId())
        .patientFullName(patient.getFirstName() + " " + patient.getLastName())
        .serviceId(additionalService.getServiceId())
        .serviceName(service.getName())
        .attachmentUrls(additionalService.getAttachmentUrls())
        .build();
  }
}
