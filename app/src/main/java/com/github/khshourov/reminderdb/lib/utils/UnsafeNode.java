package com.github.khshourov.reminderdb.lib.utils;

public class UnsafeNode<T> {
  T item;
  UnsafeNode<T> prev;
  UnsafeNode<T> next;

  public UnsafeNode(T item) {
    this.item = item;
  }

  public T getItem() {
    return item;
  }
}
