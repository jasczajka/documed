package com.documed.backend.visits;

import com.documed.backend.visits.dtos.CreateFacilityDTO;
import com.documed.backend.visits.dtos.FacilityInfoReturnDTO;
import com.documed.backend.visits.model.Facility;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FacilityService {

  private final FacilityDAO facilityDAO;

  List<Facility> getAll() {
    return facilityDAO.getAll();
  }

  public Optional<Facility> getById(int id) {
    return facilityDAO.getById(id);
  }

  public FacilityInfoReturnDTO create(CreateFacilityDTO dto) {

    Facility facility = Facility.builder().address(dto.getAddress()).city(dto.getCity()).build();

    facility = facilityDAO.create(facility);

    return FacilityInfoReturnDTO.builder()
        .id(facility.getId())
        .address(facility.getAddress())
        .city(facility.getCity())
        .build();
  }
}
