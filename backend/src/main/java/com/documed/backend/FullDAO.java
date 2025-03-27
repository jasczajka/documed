package com.documed.backend;

import java.sql.SQLException;

public interface FullDAO<T> extends ReadDAO<T> {

  int create(T obj) throws SQLException;

  int update(T obj) throws SQLException;

  int delete(int id) throws SQLException;
}
