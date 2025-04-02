package com.documed.backend.visits;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FacilityService {

  private final FacilityDAO facilityDAO;

  List<Facility> getAll() throws SQLException {
    return facilityDAO.getAll();
  }

  Optional<Facility> getById(int id) throws SQLException {
    return facilityDAO.getById(id);
  }
}
