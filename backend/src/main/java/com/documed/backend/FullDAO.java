package com.documed.backend;

public interface FullDAO<T> extends ReadDAO<T> {

  T create(T obj);

  int delete(int id);
}
