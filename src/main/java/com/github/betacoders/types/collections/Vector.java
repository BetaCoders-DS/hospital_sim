package com.github.betacoders.types.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Vector
 * Implementação genérica de um vetor dinâmico
 */
public class Vector<T> implements Iterable<T> {
  public final int INIT_CAP = 2;
  public final int CAP_MULT = 2;
  private int size;
  private int cap = INIT_CAP;
  private Object[] data;

  public Vector() {
    data = new Object[cap];
    size = 0;
  }

  public Vector(int cap) {
    data = new Object[cap];
    this.cap = cap;
    size = 0;
  }

  private void checkIndex(int i) {
    if (i < 0 || i >= cap)
      throw new ArrayIndexOutOfBoundsException(i);
  }

  public T get(int i) {
    checkIndex(i);
    return (T) data[i];
  }

  public void set(int i, T item) {
    checkIndex(i);
    data[i] = item;
    this.size = Math.max(size, i + 1);
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void push(T v) {
    if (size == cap)
      increase();
    data[size++] = v;
  }

  public T pop() {
    if (isEmpty())
      return null;
    T out = (T) data[--size];
    data[size] = null;
    return out;
  }

  public T peek() {
    if (isEmpty())
      return null;
    return (T) data[size - 1];
  }

  private void increase() {
    resize(Math.max(cap * CAP_MULT, cap + 1));
  }

  public int size() {
    return size;
  }

  public int cap() {
    return cap;
  }

  public void resize(int cap) {
    data = Arrays.copyOf(data, cap);
    if (size > cap)
      size = cap;
    this.cap = cap;
  }

  public boolean contains(T item) {
    return indexOf(item) != -1;
  }

  public int indexOf(T item) {
    for (int i = 0; i < size; ++i) {
      if (data[i] == item)
        return i;
    }
    return -1;
  }

  public void insert(int i, T item) {
    if (i < 0 || i > size)
      throw new ArrayIndexOutOfBoundsException(i);
    if (size == cap)
      increase();
    for (int j = size; j > i; --j)
      data[j] = data[j - 1];
    data[i] = item;
    ++size;
  }

  public T removeAt(int i) {
    if (i < 0 || i >= size)
      throw new ArrayIndexOutOfBoundsException(i);
    T out = (T) data[i];
    for (int j = i; j < size - 1; ++j)
      data[j] = data[j + 1];
    data[--size] = null;
    return out;
  }

  public void clear() {
    Arrays.fill(data, 0, size, null);
    size = 0;
  }

  private class VectorIterator implements Iterator<T> {
    private int idx = 0;

    @Override
    public boolean hasNext() {
      return idx < size;
    }

    @Override
    public T next() {
      if (idx >= size)
        throw new NoSuchElementException();
      return (T) data[idx++];
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new VectorIterator();
  }
}
