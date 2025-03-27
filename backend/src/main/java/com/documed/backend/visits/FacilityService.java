package com.documed.backend.visits;

import java.sql.SQLException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FacilityService {

  FacilityDAO facilityDAO;

  List<Facility> getAll() throws SQLException {
    return facilityDAO.getAll();
  }

  Facility getById(int id) throws SQLException {
    return facilityDAO.getById(id);
  }
}
