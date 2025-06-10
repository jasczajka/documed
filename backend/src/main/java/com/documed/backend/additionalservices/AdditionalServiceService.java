package com.documed.backend.additionalservices;

import com.documed.backend.additionalservices.model.AdditionalService;
import com.documed.backend.additionalservices.model.AdditionalServiceWithDetails;
import com.documed.backend.attachments.AttachmentDAO;
import com.documed.backend.attachments.S3Service;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.exceptions.InvalidAssignmentException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.ServiceDAO;
import com.documed.backend.services.model.ServiceType;
import com.documed.backend.users.UserDAO;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AdditionalServiceService {
  private final AdditionalServiceDAO additionalServiceDAO;
  private final S3Service s3Service;
  private final AttachmentDAO attachmentDAO;
  private final UserDAO userDAO;
  private final ServiceDAO serviceDAO;

  public List<AdditionalServiceWithDetails> getAllWithDetailsBetweenDates(LocalDate startDate) {
    return additionalServiceDAO.findAllWithDetailsBetweenDates(startDate);
  }

  public AdditionalServiceWithDetails getByIdWithDetails(int id) {
    return additionalServiceDAO
        .findByIdWithDetails(id)
        .orElseThrow(() -> new NotFoundException("Additional service not found"));
  }

  public List<AdditionalServiceWithDetails> getByPatientIdWithDetailsBetweenDates(
      int patientId, LocalDate startDate) {
    return additionalServiceDAO.findByPatientIdWithDetailsBetweenDates(patientId, startDate);
  }

  public List<AdditionalServiceWithDetails> getByFulfillerIdWithDetailsBetweenDates(
      int fulfillerId, LocalDate startDate) {
    return additionalServiceDAO.findByFulfillerIdWithDetailsBetweenDates(fulfillerId, startDate);
  }

  public List<AdditionalServiceWithDetails> getByServiceIdWithDetailsBetweenDates(
      int serviceId, LocalDate startDate) {
    return additionalServiceDAO.findByServiceIdWithDetailsBetweenDates(serviceId, startDate);
  }

  public List<AdditionalServiceWithDetails> getByPatientIdAndFulfillerIdWithDetailsBetweenDates(
      int patientId, int fulfillerId, LocalDate startDate) {
    return additionalServiceDAO.findByPatientIdAndFulfillerIdWithDetailsBetweenDates(
        patientId, fulfillerId, startDate);
  }

  public List<AdditionalService> getAll() {
    return additionalServiceDAO.getAll();
  }

  public AdditionalService getById(int additionalServiceId) {
    return additionalServiceDAO
        .getById(additionalServiceId)
        .orElseThrow(() -> new NotFoundException("Additional service not found"));
  }

  @Transactional
  public AdditionalService createAdditionalService(
      String description, LocalDate date, int patientId, int fulfillerId, int serviceId) {

    User fulfiller =
        userDAO
            .getById(fulfillerId)
            .orElseThrow(() -> new NotFoundException("Fulfiller not found"));

    if (fulfiller.getRole() == UserRole.PATIENT) {
      throw new BadRequestException("Patient cannot be a fulfiller");
    }

    User patient =
        userDAO.getById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));
    if (patient.getRole() != UserRole.PATIENT) {
      throw new BadRequestException("Patient has to be a patient");
    }

    com.documed.backend.services.model.Service service =
        serviceDAO.getById(serviceId).orElseThrow(() -> new NotFoundException("Service not found"));
    if (service.getType() != ServiceType.ADDITIONAL_SERVICE) {
      throw new InvalidAssignmentException("This service is not of type additional service");
    }

    AdditionalService additionalService =
        AdditionalService.builder()
            .description(description)
            .date(date)
            .fulfillerId(fulfillerId)
            .patientId(patientId)
            .serviceId(serviceId)
            .build();

    return additionalServiceDAO.create(additionalService);
  }

  @Transactional
  public int deleteAdditionalService(int additionalServiceId) {
    List<Attachment> attachments =
        s3Service.getAttachmentsForAdditionalService(additionalServiceId);
    attachments.forEach(attachment -> s3Service.deleteFile(attachment.getId()));

    return additionalServiceDAO.delete(additionalServiceId);
  }

  public List<AdditionalService> getByFulfiller(int fulfillerId) {
    userDAO.getById(fulfillerId).orElseThrow(() -> new NotFoundException("Fulfiller not found"));
    return additionalServiceDAO.getByFulfillerId(fulfillerId);
  }

  public List<AdditionalService> getByService(int serviceId) {
    serviceDAO.getById(serviceId).orElseThrow(() -> new NotFoundException("Service not found"));
    return additionalServiceDAO.getByServiceId(serviceId);
  }

  public List<AdditionalService> getByPatient(int patientId) {
    userDAO.getById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));
    return additionalServiceDAO.getByPatientId(patientId);
  }

  @Transactional
  public AdditionalService updateAttachmentsForAdditionalService(
      int additionalServiceId, List<Integer> updatedAttachmentIds) {
    List<Attachment> currentAttachments =
        s3Service.getAttachmentsForAdditionalService(additionalServiceId);
    List<Integer> currentIds = currentAttachments.stream().map(Attachment::getId).toList();

    List<Integer> toDelete =
        currentIds.stream().filter(id -> !updatedAttachmentIds.contains(id)).toList();

    List<Integer> toAssign =
        updatedAttachmentIds.stream().filter(id -> !currentIds.contains(id)).toList();

    toDelete.forEach(s3Service::deleteFile);

    toAssign.forEach(id -> attachmentDAO.assignToAdditionalService(id, additionalServiceId));
    return this.getById(additionalServiceId);
  }

  public void updateDescription(int id, String newDescription) {
    this.additionalServiceDAO
        .getById(id)
        .orElseThrow(() -> new NotFoundException("Additional service not found"));
    this.additionalServiceDAO.updateDescription(id, newDescription);
  }
}
