package com.documed.backend.services;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.users.Specialisation;
import com.documed.backend.visits.Visit;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Service {
    private final int id;
    @NonNull
    private String name;
    @NonNull
    private BigDecimal price;
    @NonNull
    private ServiceType type;
    private int estimatedTime;
    private List<Specialisation> specialisations;
    private List<Visit> visits;
    private List<AdditionalService> additionalServices;
}
