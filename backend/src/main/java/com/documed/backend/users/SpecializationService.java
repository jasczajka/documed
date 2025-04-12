package com.documed.backend.users;

import java.util.List;
import java.util.Optional;

import com.documed.backend.users.model.Specialization;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecializationService {

  private final SpecializationDAO specializationDAO;

  List<Specialization> getAll() {
    return specializationDAO.getAll();
  }

  Optional<Specialization> getById(int id) {
    return specializationDAO.getById(id);
  }
}
