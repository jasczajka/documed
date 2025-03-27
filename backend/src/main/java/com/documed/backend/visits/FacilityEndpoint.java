package com.documed.backend.visits;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(("/api/facilities"))
public class FacilityEndpoint {

    FacilityService facilityService;

    @GetMapping
    public List<Facility> getAllFacilities() throws SQLException {
        return facilityService.getAll();
    }

    @GetMapping("/{id}")
    public Facility getFacility(@PathVariable int id) throws SQLException {
        return facilityService.getById(id);
    }

}
