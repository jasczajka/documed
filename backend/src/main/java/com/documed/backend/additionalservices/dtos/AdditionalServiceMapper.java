package com.documed.backend.additionalservices.dtos;

import com.documed.backend.additionalservices.model.AdditionalService;

public class AdditionalServiceMapper {

  public static AdditionalServiceReturnDTO toDto(AdditionalService service) {
    if (service == null) {
      return null;
    }

    return AdditionalServiceReturnDTO.builder()
        .id(service.getId())
        .description(service.getDescription())
        .date(service.getDate())
        .fulfillerId(service.getFulfiller().getId())
        .patientId(service.getPatient().getId())
        .serviceId(service.getService().getId())
        .attachmentUrls(service.getAttachmentUrls())
        .build();
  }
}
