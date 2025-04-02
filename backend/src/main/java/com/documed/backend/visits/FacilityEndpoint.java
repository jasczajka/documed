package com.documed.backend.visits;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityEndpoint {

  FacilityService facilityService;

  @GetMapping
  public ResponseEntity<List<Facility>> getAllFacilities() {
    List<Facility> facilities = facilityService.getAll();
    if(facilities.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(facilities);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Facility> getFacility(@PathVariable int id) {
    Optional<Facility> facility = facilityService.getById(id);
    if(facility.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(facility.get());
    }
  }
}
