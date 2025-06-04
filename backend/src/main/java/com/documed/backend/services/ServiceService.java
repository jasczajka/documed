package com.documed.backend.services;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.model.ServiceType;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.services.SubscriptionService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ServiceService {

  private final ServiceDAO serviceDAO;
  private final SubscriptionService subscriptionService;

  public List<com.documed.backend.services.model.Service> getAll() {
    return serviceDAO.getAll();
  }

  public Optional<com.documed.backend.services.model.Service> getById(int id) {
    return serviceDAO.getById(id);
  }

  @Transactional
  public com.documed.backend.services.model.Service createService(
      String name,
      BigDecimal price,
      ServiceType type,
      int estimatedTime,
      List<Integer> specializationIds) {

    com.documed.backend.services.model.Service service =
        com.documed.backend.services.model.Service.builder()
            .name(name)
            .price(price)
            .type(type)
            .estimatedTime(estimatedTime)
            .build();

    com.documed.backend.services.model.Service createdService = serviceDAO.create(service);
    addSpecializationsToService(createdService.getId(), specializationIds);

    if (type == ServiceType.REGULAR_SERVICE) {
      subscriptionService.createSubscriptionToServiceForNewService(createdService.getId());
    }

    return createdService;
  }

  @Transactional
  public int delete(int serviceId) {
    removeAllSpecializationFromService(serviceId);
    subscriptionService.deleteSubscriptionToServiceForService(serviceId);
    return serviceDAO.delete(serviceId);
  }

  com.documed.backend.services.model.Service updatePrice(int serviceId, BigDecimal price) {
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be greater than zero");
    }
    return serviceDAO.updatePrice(serviceId, price);
  }

  com.documed.backend.services.model.Service updateEstimatedTime(int serviceId, int time) {
    if (time <= 0) {
      throw new IllegalArgumentException("Time must be greater than zero");
    }
    return serviceDAO.updateEstimatedTime(serviceId, time);
  }

  Specialization addSpecializationToService(int serviceId, int specializationId) {
    return serviceDAO.addSpecializationToService(serviceId, specializationId);
  }

  com.documed.backend.services.model.Service addSpecializationsToService(
      int serviceId, List<Integer> specializationIds) {
    return serviceDAO.addSpecializationsToService(serviceId, specializationIds);
  }

  int removeSpecializationFromService(int serviceId, int specializationId) {
    return serviceDAO.removeSpecializationFromService(serviceId, specializationId);
  }

  void removeAllSpecializationFromService(int serviceId) {
    serviceDAO.removeAllSpecializationFromService(serviceId);
  }

  public BigDecimal getPriceForService(int serviceId) {
    return serviceDAO
        .getById(serviceId)
        .map(com.documed.backend.services.model.Service::getPrice)
        .orElseThrow(() -> new NotFoundException("Service not found"));
  }
}
