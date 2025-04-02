package com.documed.backend.users;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
