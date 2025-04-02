package com.documed.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/services")
public class ServiceEndpoint {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() throws SQLException {
        List<Service> services = serviceService.getAll();
        if (services.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(services, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Service>> getService(@PathVariable int id) throws SQLException {
        Optional<Service> service = serviceService.getById(id);
        if(service.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(service, HttpStatus.OK);
        }
    }

    @PostMapping()
    public ResponseEntity<String> createService(@RequestBody Service service) {
        try {
            int result = serviceService.create(service.getName(), service.getPrice(), service.getType(), service.getEstimatedTime());
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Service created successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create service.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable int id) {
        try {
            int result = serviceService.delete(id);
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Service deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete service.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<String> updateServicePrice(@PathVariable int id, @RequestBody BigDecimal price) {
        try {
            int result = serviceService.updatePrice(id, price);
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Service updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update service.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update service. Illegal argument.");
        }
    }

    @PatchMapping("/{id}/time")
    public ResponseEntity<String> updateServiceTime(@PathVariable int id, @RequestBody int estimatedTime) {
        try {
            int result = serviceService.updateEstimatedTime(id, estimatedTime);
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Service updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update service.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update service. Illegal argument.");
        }
    }

}
