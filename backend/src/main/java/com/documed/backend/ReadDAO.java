package com.documed.backend;

import java.util.List;
import java.util.Optional;

public interface ReadDAO<T> {

  Optional<T> getById(int id);

  List<T> getAll();
}
