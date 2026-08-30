package com.github.betacoders.types.collections;

/**
 * Tree
 */
public interface Tree<T extends Comparable<T>> extends Iterable<T> {
  public T insert(T val);

  public boolean contains(T val);
}
