package com.documed.backend.users;

import com.documed.backend.services.Service;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class Specialization {
  private final int id;
  @NonNull private String name;
  private List<Service> services;
  private List<User> users;
}
