package com.documed.backend.users;

import com.documed.backend.services.Service;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Specialisation {
    private final int id;
    @NonNull
    private String name;
    private List<Service> services;
    private List<User> users;
}
