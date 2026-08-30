package com.github.betacoders.types.collections;

import java.util.Arrays;

/**
 * Vector
 */
public class Vector<T> {
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

  public T get(int i) {
    return (T) data[i];
  }

  public void set(int i, T item) {
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
    resize(cap * CAP_MULT);
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
}
