package com.github.betacoders.types.collections;

/**
 * List
 */
public interface List<T> extends Iterable<T> {
  T get(int i);

  void set(int i, T val);

  boolean isEmpty();

  void add(T item);

  void remove(T item);

  void insert(int i, T item);

  int size();
}
