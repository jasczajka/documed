package com.documed.backend.visits;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FacilityService {

  FacilityDAO facilityDAO;

  List<Facility> getAll() {
    return facilityDAO.getAll();
  }

  Optional<Facility> getById(int id) {
    return facilityDAO.getById(id);
  }
}
