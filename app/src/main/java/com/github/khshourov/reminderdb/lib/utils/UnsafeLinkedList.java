package com.github.khshourov.reminderdb.lib.utils;

import java.util.Iterator;

public class UnsafeLinkedList<T> {
  private UnsafeNode<T> head;
  private UnsafeNode<T> end;

  public UnsafeNode<T> add(T item) {
    UnsafeNode<T> newNode = new UnsafeNode<>(item);

    if (this.head != null) {
      newNode.prev = this.end;

      this.end.next = newNode;
      this.end = newNode;
    } else {
      this.head = this.end = newNode;
    }

    return newNode;
  }

  public boolean delete(UnsafeNode<T> node) {
    if (node == null) {
      return false;
    }
    if (this.head != node && this.end != node) {
      // We're assuming that this `node` instance is originally
      // given by `add` method. Without this assumption, we can
      // not make the following code works.
      node.prev.next = node.next;
      node.next.prev = node.prev;
    }
    if (this.head == node && this.end != node) {
      node.next.prev = null;
      this.head = node.next;
    }
    if (this.head != node && this.end == node) {
      node.prev.next = null;
      this.end = node.prev;
    }
    if (this.head == node && this.end == node) {
      this.head = this.end = null;
    }

    node.prev = node.next = null;

    return true;
  }

  public void deleteAll() {
    UnsafeNode<T> curr = this.head;
    while (curr != null) {
      UnsafeNode<T> temp = curr.next;
      curr.prev = curr.next = null;
      curr = temp;
    }

    this.head = this.end = null;
  }

  public boolean isEmpty() {
    return this.head == null && this.end == null;
  }

  public Iterator<T> iterator() {
    return new UnsafeLinkedListIterator();
  }

  private class UnsafeLinkedListIterator implements Iterator<T> {
    private UnsafeNode<T> curr;

    public UnsafeLinkedListIterator() {
      this.curr = UnsafeLinkedList.this.head;
    }

    @Override
    public boolean hasNext() {
      return this.curr != null;
    }

    @Override
    public T next() {
      T item = this.curr.item;
      this.curr = this.curr.next;
      return item;
    }
  }
}
