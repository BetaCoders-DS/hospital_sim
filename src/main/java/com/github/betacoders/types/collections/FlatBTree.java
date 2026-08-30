package com.github.betacoders.types.collections;

import java.util.Iterator;

/**
 * FlatBTree
 */
public class FlatBTree<T extends Comparable<T>> implements Tree<T> {
  protected Vector<T> nodes;
  protected int nNodes = 0;

  private static final int INIT_CAP = 2;

  public FlatBTree() {
    this(INIT_CAP);
  }

  public FlatBTree(int cap) {
    nodes = new Vector<>(cap);
  }

  public int size() {
    return nNodes;
  }

  @Override
  public T insert(T val) {
    return add(val);
  }

  private T add(T val) {
    T _node;
    int node = 0;

    while ((_node = nodes.get(node)) != null) {
      int comp = _node.compareTo(val);

      int next;
      if (comp > 0)
        next = node * 2 + 2;
      else if (comp < 0)
        next = node * 2 + 1;
      else
        return _node;

      if (next >= nodes.cap())
        nodes.resize(next + 1);

      node = next;
    }

    nodes.set(node, val);
    ++nNodes;

    return null;
  }

  @Override
  public boolean contains(T val) {
    T _node;
    int node = 0;

    while ((_node = nodes.get(node)) != null) {
      int comp = _node.compareTo(val);

      int next;
      if (comp > 0)
        next = node * 2 + 2;
      else if (comp < 0)
        next = node * 2 + 1;
      else
        return true;

      if (next >= nodes.size())
        return false;

      node = next;
    }

    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return null;
  }

  public boolean isEmpty() {
    return nodes.isEmpty();
  }
}
