package com.documed.backend.services;

import com.documed.backend.users.model.Specialization;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ServiceService {

  private final ServiceDAO serviceDAO;

  List<com.documed.backend.services.Service> getAll() {
    return serviceDAO.getAll();
  }

  Optional<com.documed.backend.services.Service> getById(int id) {
    return serviceDAO.getById(id);
  }

  com.documed.backend.services.Service createService(
      String name, BigDecimal price, ServiceType type, int estimatedTime) {

    com.documed.backend.services.Service service =
            com.documed.backend.services.Service
                    .builder()
                    .name(name)
                    .price(price)
                    .type(type)
                    .estimatedTime(estimatedTime)
                    .build();

    return serviceDAO.create(service);
  }

  int delete(int id) {
    return serviceDAO.delete(id);
  }

  com.documed.backend.services.Service updatePrice(int serviceId, BigDecimal price) {
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be greater than zero");
    }
    return serviceDAO.updatePrice(serviceId, price);
  }

  com.documed.backend.services.Service updateEstimatedTime(int serviceId, int time) {
    if (time <= 0) {
      throw new IllegalArgumentException("Time must be greater than zero");
    }
    return serviceDAO.updateEstimatedTime(serviceId, time);
  }

  Specialization addSpecializationToService(int serviceId, int specializationId) {
    return serviceDAO.addSpecializationToService(serviceId, specializationId);
  }

  int removeSpecializationFromService(int serviceId, int specializationId) {
    return serviceDAO.removeSpecializationFromService(serviceId, specializationId);
  }
}
