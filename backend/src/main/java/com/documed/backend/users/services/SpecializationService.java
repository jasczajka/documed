package com.documed.backend.users.services;

import com.documed.backend.users.SpecializationDAO;
import com.documed.backend.users.model.Specialization;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecializationService {

  private final SpecializationDAO specializationDAO;

  public List<Specialization> getAll() {
    return specializationDAO.getAll();
  }

  public Optional<Specialization> getById(int id) {
    return specializationDAO.getById(id);
  }
}
