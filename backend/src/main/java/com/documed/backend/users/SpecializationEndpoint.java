package com.documed.backend.users;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/specializations")
public class SpecializationEndpoint {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        List<Specialization> specializations = specializationService.getAll();
        if (specializations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(specializations, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialization> getSpecialization(@PathVariable int id) {
        Optional<Specialization> specialization = specializationService.getById(id);
        if (specialization.isPresent()) {
            return new ResponseEntity<>(specialization.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
