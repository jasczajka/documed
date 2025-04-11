package com.documed.backend.medicines;

import com.documed.backend.medicines.model.LiteMedicine;
import com.documed.backend.medicines.model.Medicine;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MedicineService {

  private final MedicineDAO medicineDAO;

  public List<Medicine> getAll() {
    return medicineDAO.getAll();
  }

  public Optional<Medicine> getById(int id) {
    return medicineDAO.getById(id);
  }

  public List<LiteMedicine> search(String query, int limit) {
    return medicineDAO.searchLite(query, limit);
  }

  public List<Medicine> getLimited(int limit) {
    return medicineDAO.getLimited(limit);
  }
}
