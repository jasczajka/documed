package com.documed.backend.visits;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    int create(T obj) throws SQLException;
    int update(T obj) throws SQLException;
    int delete(int id) throws SQLException;

}
