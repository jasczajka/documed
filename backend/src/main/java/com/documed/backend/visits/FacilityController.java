package com.documed.backend.visits;

import com.documed.backend.visits.dtos.FacilityInfoReturnDTO;
import com.documed.backend.visits.model.Facility;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityController {

  FacilityService facilityService;

  @GetMapping
  public ResponseEntity<List<FacilityInfoReturnDTO>> getAllFacilities() {
    List<Facility> facilities = facilityService.getAll();
    if (facilities.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      List<FacilityInfoReturnDTO> dtos =
          facilities.stream()
              .map(
                  facility ->
                      FacilityInfoReturnDTO.builder()
                          .id(facility.getId())
                          .address(facility.getAddress())
                          .city(facility.getCity())
                          .build())
              .toList();

      return ResponseEntity.ok(dtos);
    }
  }
}
