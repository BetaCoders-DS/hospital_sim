package com.github.betacoders.types.collections;

public class HashMap<K, V> implements Map<K, V> {
  private static final int INIT_CAP = 16;

  private Object[] keys;
  private Object[] values;
  private int size;

  public HashMap(int cap) {
    keys = new Object[cap];
    values = new Object[cap];
  }

  public HashMap() {
    this(INIT_CAP);
  }

  private int index(K key) {
    return (key.hashCode() & 0x7fffffff) % keys.length;
  }

  private void grow() {
    Object[] oldKeys = keys;
    Object[] oldValues = values;
    int oldCap = keys.length;
    keys = new Object[oldCap * 2];
    values = new Object[oldCap * 2];
    size = 0;
    for (int i = 0; i < oldCap; ++i) {
      if (oldKeys[i] != null) {
        put((K) oldKeys[i], (V) oldValues[i]);
      }
    }
  }

  @Override
  public V get(K key) {
    if (key == null) {
      return null;
    }
    int i = index(key);
    while (keys[i] != null) {
      if (key.equals(keys[i])) {
        return (V) values[i];
      }
      i = (i + 1) % keys.length;
    }
    return null;
  }

  @Override
  public V put(K key, V value) {
    if (key == null) {
      return null;
    }
    if ((size + 1) * 4 >= keys.length * 3) {
      grow();
    }
    int i = index(key);
    while (keys[i] != null) {
      if (key.equals(keys[i])) {
        V old = (V) values[i];
        values[i] = value;
        return old;
      }
      i = (i + 1) % keys.length;
    }
    keys[i] = key;
    values[i] = value;
    ++size;
    return null;
  }
}