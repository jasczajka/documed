package com.documed.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServiceService {

    ServiceDAO serviceDAO;

    List<com.documed.backend.services.Service> getAll() throws SQLException {
        return serviceDAO.getAll();
    }

    Optional<com.documed.backend.services.Service> getById(int id) throws SQLException {
        return serviceDAO.getById(id);
    }

    int create(String name, BigDecimal price, ServiceType type, int estimatedTime) throws SQLException {

        com.documed.backend.services.Service service = new com.documed.backend.services.Service(name, price, type, estimatedTime);

        return serviceDAO.create(service);
    }

    int delete(int id) throws SQLException {
        return serviceDAO.delete(id);
    }

    int updatePrice(int serviceId, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        return serviceDAO.updatePrice(serviceId, price);
    }

    int updateEstimatedTime(int serviceId, int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time must be greater than zero");
        }
        return serviceDAO.updateEstimatedTime(serviceId, time);
    }

}
