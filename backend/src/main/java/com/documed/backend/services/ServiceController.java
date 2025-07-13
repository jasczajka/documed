package com.documed.backend.services;

import com.documed.backend.auth.annotations.AdminOnly;
import com.documed.backend.services.dtos.CreateServiceDTO;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.model.Specialization;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/services")
public class ServiceController {

  private final ServiceService serviceService;

  @GetMapping
  public ResponseEntity<List<Service>> getAllServices() {
    List<Service> services = serviceService.getAll();
    return new ResponseEntity<>(services, HttpStatus.OK);
  }

  @GetMapping("/regular")
  public ResponseEntity<List<Service>> getAllRegularServices() {
    List<Service> services = serviceService.getAllRegular();
    return new ResponseEntity<>(services, HttpStatus.OK);
  }

  @GetMapping("/additional")
  public ResponseEntity<List<Service>> getAllAdditionalServices() {
    List<Service> services = serviceService.getAllAdditional();
    return new ResponseEntity<>(services, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Optional<Service>> getService(@PathVariable int id) {
    Optional<Service> service = serviceService.getById(id);
    if (service.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(service, HttpStatus.OK);
    }
  }

  @AdminOnly
  @Transactional
  @PostMapping()
  public ResponseEntity<Service> createService(@RequestBody CreateServiceDTO createServiceDTO) {
    Service createdService =
        serviceService.createService(
            createServiceDTO.getName(),
            createServiceDTO.getPrice(),
            createServiceDTO.getType(),
            createServiceDTO.getEstimatedTime(),
            createServiceDTO.getSpecializationIds());

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/services/" + createdService.getId()));

    return new ResponseEntity<>(createdService, headers, HttpStatus.CREATED);
  }

  @AdminOnly
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteService(@PathVariable int id) {
    int result = serviceService.delete(id);
    if (result > 0) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Service deleted successfully.");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to delete service.");
    }
  }

  @AdminOnly
  @PatchMapping("/{id}/price")
  public ResponseEntity<Service> updateServicePrice(
      @PathVariable int id, @RequestBody BigDecimal price) {
    Service updatedService = serviceService.updatePrice(id, price);
    return new ResponseEntity<>(updatedService, HttpStatus.OK);
  }

  @AdminOnly
  @PatchMapping("/{id}/time")
  public ResponseEntity<Service> updateServiceTime(
      @PathVariable int id, @RequestBody int estimatedTime) {
    Service updatedService = serviceService.updateEstimatedTime(id, estimatedTime);
    return new ResponseEntity<>(updatedService, HttpStatus.CREATED);
  }

  @AdminOnly
  @PostMapping("/{id}/specialization")
  public ResponseEntity<Specialization> addSpecializationToService(
      @PathVariable int id, @RequestBody int specializationId) {
    Specialization addedSpecialization =
        serviceService.addSpecializationToService(id, specializationId);
    return new ResponseEntity<>(addedSpecialization, HttpStatus.OK);
  }

  @AdminOnly
  @DeleteMapping("/{id}/specialization")
  public ResponseEntity<String> removeSpecializationFromService(
      @PathVariable int id, @RequestBody int specializationId) {
    int result = serviceService.removeSpecializationFromService(id, specializationId);
    if (result > 0) {
      return ResponseEntity.status(HttpStatus.OK).body("Removed specialization successfully.");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to remove specialization from service.");
    }
  }

  @GetMapping("/calculate-cost")
  @Operation(summary = "Calculate service cost")
  public ResponseEntity<BigDecimal> calculateServiceCost(
      @RequestParam int patientId, @RequestParam int serviceId) {
    return new ResponseEntity<>(
        serviceService.calculateTotalCost(serviceId, patientId), HttpStatus.OK);
  }
}
