package com.documed.backend.services;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.users.Specialisation;
import com.documed.backend.visits.Visit;

import java.util.List;

public class Service {
    private int id;
    private String name;
    private float price;
    private ServiceType type;
    private int estimatedTime;
    private List<Specialisation> specialisations;
    private List<Visit> visits;
    private List<AdditionalService> additionalServices;
}
