package com.documed.backend.users;

import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.services.SpecializationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/specializations")
public class SpecializationController {

  private final SpecializationService specializationService;

  @GetMapping
  public ResponseEntity<List<Specialization>> getAllSpecializations() {
    List<Specialization> specializations = specializationService.getAll();
    return new ResponseEntity<>(specializations, HttpStatus.OK);
  }
}
