package com.documed.backend;

public interface FullDAO<T> extends ReadDAO<T> {

  T create(T obj);

  T update(T obj);

  int delete(int id);
}
