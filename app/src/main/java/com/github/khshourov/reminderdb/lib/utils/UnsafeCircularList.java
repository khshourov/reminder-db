package com.github.khshourov.reminderdb.lib.utils;

import java.util.Iterator;

public class UnsafeCircularList<T> {
  private final UnsafeNode<T> root;

  public UnsafeCircularList() {
    this.root = new UnsafeNode<>(null);
    this.root.prev = this.root;
    this.root.next = this.root;
  }

  public UnsafeNode<T> add(T item) {
    UnsafeNode<T> newNode = new UnsafeNode<>(item);

    this.root.prev.next = newNode;
    newNode.prev = this.root.prev;

    this.root.prev = newNode;
    newNode.next = this.root;

    return newNode;
  }

  public boolean delete(UnsafeNode<T> node) {
    if (node == null) {
      return false;
    }

    node.next.prev = node.prev;
    node.prev.next = node.next;

    node.prev = node.next = null;
    return true;
  }

  public void deleteAll() {
    UnsafeNode<T> curr = this.root.next;
    while (curr != this.root) {
      UnsafeNode<T> next = curr.next;

      curr.prev = curr.next = null;

      curr = next;
    }

    this.root.prev = this.root;
    this.root.next = this.root;
  }

  public boolean isEmpty() {
    return this.root.prev == this.root && this.root.next == this.root;
  }

  public Iterator<T> iterator() {
    return new UnsafeCircularListIterator();
  }

  private class UnsafeCircularListIterator implements Iterator<T> {
    private UnsafeNode<T> curr;

    public UnsafeCircularListIterator() {
      this.curr = UnsafeCircularList.this.root.next;
    }

    @Override
    public boolean hasNext() {
      return this.curr != UnsafeCircularList.this.root;
    }

    @Override
    public T next() {
      T item = this.curr.item;
      this.curr = this.curr.next;
      return item;
    }
  }
}
