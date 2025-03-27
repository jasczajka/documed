package com.documed.backend;

import java.sql.SQLException;
import java.util.List;

public interface ReadDAO<T> {

  T getById(int id) throws SQLException;

  List<T> getAll() throws SQLException;
}
