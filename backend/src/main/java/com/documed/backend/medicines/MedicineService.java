package com.documed.backend.medicines;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MedicineService {

    private final MedicineDAO medicineDAO;

    List<Medicine> getAll() {
        return medicineDAO.getAll();
    }

    Optional<Medicine> getById(int id) {
        return medicineDAO.getById(id);
    }


}
