package com.documed.backend;

public interface FullDAO<T> extends ReadDAO<T> {

  int create(T obj);

  int update(T obj);

  int delete(int id);
}
