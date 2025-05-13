package com.documed.backend.visits;

import com.documed.backend.visits.model.Visit;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    @GetMapping("/{id}")
    @Operation(summary = "Get visit by id")
    public ResponseEntity<Optional<Visit>> getVisitById(@PathVariable int id) {
        Optional<Visit> visit = visitService.getById(id);
        if (visit.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(visit, HttpStatus.OK);
        }
    }

    @PostMapping
    @Operation(summary = "schedule/create visit")
    public ResponseEntity<Visit> scheduleVisit(@RequestBody Visit visit) {

    }

}
