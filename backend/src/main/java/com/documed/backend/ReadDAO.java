package com.documed.backend;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ReadDAO<T> {

  Optional<T> getById(int id) throws SQLException;

  List<T> getAll() throws SQLException;
}
