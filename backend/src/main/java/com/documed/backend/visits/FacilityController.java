package com.documed.backend.visits;

import com.documed.backend.visits.dtos.FacilityLoginReturnDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityController {

  FacilityService facilityService;

  @GetMapping
  public ResponseEntity<List<FacilityLoginReturnDTO>> getAllFacilities() {
    List<Facility> facilities = facilityService.getAll();
    if (facilities.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      List<FacilityLoginReturnDTO> dtos =
          facilities.stream()
              .map(
                  facility ->
                      FacilityLoginReturnDTO.builder()
                          .id(facility.getId())
                          .address(facility.getAddress())
                          .city(facility.getCity())
                          .build())
              .toList();

      return ResponseEntity.ok(dtos);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Facility> getFacility(@PathVariable int id) {
    return facilityService
        .getById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
