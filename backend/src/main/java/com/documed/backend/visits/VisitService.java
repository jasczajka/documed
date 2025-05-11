package com.documed.backend.visits;

import com.documed.backend.visits.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VisitService {

    private final VisitDAO visitDAO;

    Optional<Visit> getById(int id) {
        return visitDAO.getById(id);
    }
}
