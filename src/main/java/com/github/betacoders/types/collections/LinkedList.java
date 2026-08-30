package com.github.betacoders.types.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * LinkedList
 * Classe genérica de lista linkada.
 * Deverá ser usada para as filas da simulação
 */
public class LinkedList<T> implements Iterable<T> {
  private class Node {
    private T item;
    private Node prev = null;
    private Node next = null;

    private Node(T item) {
      this.item = item;
    }
  }

  private Node head = null;
  private Node tail = null;
  private int size = 0;

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void addFirst(T item) {
    Node n = new Node(item);
    if (head == null) {
      head = tail = n;
    } else {
      head.prev = n;
      n.next = head;
      head = n;
    }
    ++size;
  }

  public void addLast(T item) {
    Node n = new Node(item);
    if (tail == null) {
      head = tail = n;
    } else {
      tail.next = n;
      n.prev = tail;
      tail = n;
    }
    ++size;
  }

  public T peekFirst() {
    return head == null ? null : head.item;
  }

  public T peekLast() {
    return tail == null ? null : tail.item;
  }

  public T removeFirst() {
    if (head == null)
      return null;
    T i = head.item;
    remove(head);
    return i;
  }

  public T removeLast() {
    if (tail == null)
      return null;
    T i = tail.item;
    remove(tail);
    return i;
  }

  public boolean contains(T item) {
    return indexOf(item) != -1;
  }

  public int indexOf(T item) {
    int idx = 0;
    Node n = head;
    while (n != null) {
      if (n.item == item)
        return idx;
      n = n.next;
      ++idx;
    }
    return -1;
  }

  public boolean remove(T item) {
    if (item == null)
      return false;
    Node n = head;
    while (n != null) {
      if (n.item == item) {
        remove(n);
        return true;
      }
      n = n.next;
    }
    return false;
  }

  private void remove(Node n) {
    if (n.next != null)
      n.next.prev = n.prev;
    if (n.prev != null)
      n.prev.next = n.next;
    if (n == head)
      head = n.next;
    if (n == tail)
      tail = n.prev;
    n.next = null;
    n.prev = null;
    --size;
  }

  private Node nodeAt(int i) {
    Node n;
    if (i <= (size >> 1)) {
      n = head;
      while (i > 0 && n != null) {
        n = n.next;
        --i;
      }
    } else {
      n = tail;
      int j = size - i - 1;
      while (j > 0 && n != null) {
        n = n.prev;
        --j;
      }
    }
    return n;
  }

  public T get(int i) {
    Node n = nodeAt(i);
    return n == null ? null : n.item;
  }

  public T remove(int i) {
    Node n = nodeAt(i);
    if (n == null)
      return null;
    T item = n.item;
    remove(n);
    return item;
  }

  public void clear() {
    head = tail = null;
    size = 0;
  }

  private class ListIterator implements Iterator<T> {
    private Node cur = head;

    @Override
    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public T next() {
      if (cur == null)
        throw new NoSuchElementException();
      T item = cur.item;
      cur = cur.next;
      return item;
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new ListIterator();
  }
}
