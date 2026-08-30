package com.github.betacoders.types.collections;

import java.util.Iterator;

/*
 * BTree
 * Classe genérica para armazenamento de objetos em uma árvore binária linkada.
 * Provavelmente não será usada
 */
public class BTree<T extends Comparable<T>> implements Tree<T> {
  protected Node root = null;

  protected class Node {
    T val;
    Node l = null;
    Node r = null;

    Node(T val) {
      this.val = val;
    }
  }

  public T insert(T val) {
    if (root == null) {
      root = new Node(val);
      return null;
    }

    return add(root, val);
  }

  protected T add(Node node, T val) {
    while (node != null) {
      int comp = val.compareTo(node.val);

      if (comp > 0) {
        if (node.r != null)
          node = node.r;
        else {
          node.r = new Node(val);
          break;
        }
      }

      else if (comp < 0) {
        if (node.l != null)
          node = node.l;
        else {
          node.l = new Node(val);
          break;
        }
      }

      else
        return node.val;
    }
    return null;
  }

  public boolean contains(T val) {
    Node n = root;

    while (n != null) {
      int c = val.compareTo(n.val);

      if (c > 0)
        n = n.r;
      else if (c < 0)
        n = n.l;
      else
        return true;
    }

    return false;
  }

  private abstract class BTreeIterator implements Iterator<T> {
    protected Vector<Node> nodes = new Vector<>();

    @Override
    public boolean hasNext() {
      return !nodes.isEmpty();
    }

    protected void addLeftChilds(Node n) {
      while (n != null) {
        nodes.push(n);
        n = n.l;
      }
    }
  }

  private class PreOrderIter extends BTreeIterator {

    PreOrderIter() {
      nodes.push(root);
    }

    @Override
    public T next() {
      Node n = nodes.pop();

      if (n.r != null)
        nodes.push(n.r);
      if (n.l != null)
        nodes.push(n.l);

      return n.val;
    }
  }

  private class InOrderIter extends BTreeIterator {
    InOrderIter() {
      addLeftChilds(root);
    }

    @Override
    public T next() {
      Node n = nodes.pop();

      if (n.r != null)
        addLeftChilds(n.r);

      return n.val;
    }
  }

  private class PostOrderIter extends BTreeIterator {
    private Node last = null;

    PostOrderIter() {
      addLeftChilds(root);
    }

    @Override
    public T next() {
      Node n = nodes.peek();

      while (n.r != null && last != n.r) {
        addLeftChilds(n.r);
        n = nodes.peek();
      }

      n = nodes.pop();
      last = n;

      return n.val;
    }
  }

  public Iterable<T> preOrderIter() {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return new PreOrderIter();
      }
    };
  }

  public Iterable<T> inOrderIter() {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return new InOrderIter();
      }
    };
  }

  public Iterable<T> postOrderIter() {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return new PostOrderIter();
      }
    };
  }

  @Override
  public Iterator<T> iterator() {
    return inOrderIter().iterator();
  }
}
