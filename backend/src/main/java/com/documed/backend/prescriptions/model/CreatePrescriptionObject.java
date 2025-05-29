package com.documed.backend.prescriptions.model;

import java.time.LocalDate;

public record CreatePrescriptionObject(Integer visitId, LocalDate expirationDate) {}
