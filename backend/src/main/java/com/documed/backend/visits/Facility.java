package com.documed.backend.visits;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Facility {
    private final int id;
    @NonNull
    private String address;
    @NonNull
    private String city;
    private List<Visit> visits;
}
