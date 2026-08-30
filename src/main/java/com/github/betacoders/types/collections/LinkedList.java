package com.github.betacoders.types.collections;

import java.util.Iterator;

/*
 * LinkedList
 * Classe genérica de lista linkada.
 * Deverá ser usada para as filas da simulação
 */
public class LinkedList<T> implements Iterable<T> {

  public class Node {
    private T item;
    private Node prev = null;
    private Node next = null;

    private Node(T item) {
      this.item = item;
    }

    private T getNode() {
      return this.item;
    }
  }

  private Node head = null;
  private Node tail = null;
  private int size = 0;

  public int getSize() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public Node addLeast(T item) {
    Node n = new Node(item);
    if (tail == null) {
      head = tail = n;
    } else {
      tail.next = n;
      n.prev = tail;
      tail = n;
    }
    ++size;
    return n;
  }

  public void remove(Node n) {
    if (n == null)
      return;
    if (n.prev != null) {
      n.prev.next = n.next;
    } else {
      n.prev = head;
    }
    if (n.next != null) {
      n.next.prev = n.prev;
    } else {
      n.next = tail;
    }
    n.next = null;
    n.prev = null;
    --size;
  }

  public class ListInterable implements Iterator<T> {
    private Node cur = head;

    @Override
    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public T next() {
      T item = cur.item;
      cur = cur.next;
      return item;
    }

  }

  @Override
  public Iterator<T> iterator() {
    return new ListInterable();
  }
}
