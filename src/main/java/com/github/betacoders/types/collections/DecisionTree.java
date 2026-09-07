package com.github.betacoders.types.collections;

import java.lang.RuntimeException;
import java.util.function.Predicate;

/**
 * DecisionTree
 */
public class DecisionTree<T, R> {
  private Vector<Node<T, R>> nodes;

  private sealed interface Node<T, R> {
    record Decision<T, R>(Predicate<T> pred) implements Node<T, R> {
    }

    record Result<T, R>(R value) implements Node<T, R> {
    }
  }

  public R classify(T input) {
    int i = 0;

    Node<T, R> node = nodes.get(0);
    while (node != null) {
      switch (node) {
        case Node.Decision<T, R> dec -> {
          if (dec.pred().test(input))
            i = 2 * i + 1;
          else
            i = 2 * i + 2;

          if (i >= nodes.size())
            throw new NoSuchResultException();

          node = nodes.get(i);
        }

        case Node.Result<T, R> res -> {
          return res.value;
        }
      }
    }

    throw new NoSuchResultException();
  }

  public static <T, R> Builder<T, R> builder() {
    return new Builder<>();
  }

  public static class Builder<T, R> {
    private Vector<Node<T, R>> nodes = new Vector<>();

    private int cursor = 0;

    private void ensureCap(int index) {
      if (index >= nodes.cap())
        nodes.resize(index + 1);
    }

    public Builder<T, R> root(Predicate<T> test) {
      cursor = 0;
      nodes.set(cursor, new Node.Decision<T, R>(test));
      return this;
    }

    public Builder<T, R> ifTrue(Predicate<T> test) {
      cursor = cursor * 2 + 1;
      ensureCap(cursor);

      nodes.set(cursor, new Node.Decision<T, R>(test));
      return this;
    }

    public Builder<T, R> ifFalse(Predicate<T> test) {
      cursor = cursor * 2 + 2;
      ensureCap(cursor);

      nodes.set(cursor, new Node.Decision<T, R>(test));
      return this;
    }

    public Builder<T, R> leafTrue(R result) {
      cursor = cursor * 2 + 1;
      ensureCap(cursor);

      nodes.set(cursor, new Node.Result<T, R>(result));
      return this;
    }

    public Builder<T, R> leafFalse(R result) {
      cursor = cursor * 2 + 2;
      ensureCap(cursor);

      nodes.set(cursor, new Node.Result<T, R>(result));
      return this;
    }

    public Builder<T, R> up() {
      cursor = (cursor - 1) >> 1;
      return this;
    }

    public DecisionTree<T, R> build() {
      return new DecisionTree<T, R>(nodes);
    }
  }

  private DecisionTree(Vector<Node<T, R>> nodes) {
    this.nodes = nodes;
  }

  public static class NoSuchResultException extends RuntimeException {
    NoSuchResultException() {
      super("No result for input.");
    }
  }

}
