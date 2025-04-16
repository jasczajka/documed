package com.documed.backend;

public interface FullDAO<T, C> extends ReadDAO<T> {

  T create(C creationObject);

  int delete(int id);
}
