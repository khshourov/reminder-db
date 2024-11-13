package com.github.khshourov.reminderdb.lib.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UnsafeLinkedListTest {
  UnsafeLinkedList<Integer> list;

  @BeforeEach
  void init() {
    list = new UnsafeLinkedList<>();
  }

  @Nested
  class WhenAddingNode {

    @Test
    void anItemShouldBeAddedWhenListIsEmpty() {
      UnsafeNode<Integer> itemNode = list.add(1);

      assertEquals(1, itemNode.item);
    }

    @Test
    void anItemShouldBeAddedWhenListContainsItems() {
      UnsafeNode<Integer> itemNode1 = list.add(1);
      UnsafeNode<Integer> itemNode2 = list.add(2);

      assertEquals(1, itemNode1.item);
      assertEquals(2, itemNode2.item);

      // It's antipattern to test internal properties. But because UnsafeNode exposes properties
      // and we've to ensure if our expectation is met i.e. itemNode1 -> itemNode2
      assertEquals(itemNode2, itemNode1.next);
      assertEquals(itemNode1, itemNode2.prev);
    }
  }

  @Nested
  class WhenDeletingNode {

    @Test
    void falseShouldTrueWhenDeletableNodeIsNull() {
      boolean deleted = list.delete(null);

      assertFalse(deleted);
    }

    @Test
    void listShouldBeEmptyWhenTheSingleNodeIsDeleted() {
      UnsafeNode<Integer> itemNode = list.add(1);

      boolean deleted = list.delete(itemNode);

      assertTrue(deleted);
      assertTrue(list.isEmpty());
    }

    @Test
    void listShouldPreserveOrderWhenFirstNodeIsDeleted() {
      UnsafeNode<Integer> itemNode1 = list.add(1);
      list.add(2);
      list.add(3);

      boolean deleted = list.delete(itemNode1);

      assertTrue(deleted);

      Iterator<Integer> iterator = list.iterator();

      assertTrue(iterator.hasNext());
      assertEquals(2, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(3, iterator.next());
      assertFalse(iterator.hasNext());
    }

    @Test
    void listShouldPreserveOrderWhenAnyNodeBetweenFirstAndLastIsDeleted() {
      list.add(1);
      UnsafeNode<Integer> itemNode2 = list.add(2);
      list.add(3);

      boolean deleted = list.delete(itemNode2);

      assertTrue(deleted);

      Iterator<Integer> iterator = list.iterator();

      assertTrue(iterator.hasNext());
      assertEquals(1, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(3, iterator.next());
      assertFalse(iterator.hasNext());
    }

    @Test
    void listShouldPreserveOrderWhenLastNodeIsDeleted() {
      list.add(1);
      list.add(2);
      UnsafeNode<Integer> itemNode3 = list.add(3);

      boolean deleted = list.delete(itemNode3);

      assertTrue(deleted);

      Iterator<Integer> iterator = list.iterator();

      assertTrue(iterator.hasNext());
      assertEquals(1, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(2, iterator.next());
      assertFalse(iterator.hasNext());
    }
  }

  @Nested
  class WhenDeletingAll {

    @Test
    void shouldBeSafeToCallWhenListIsEmpty() {
      list.deleteAll();

      assertTrue(list.isEmpty());
    }

    @Test
    void shouldDeleteAllWhenListContainsSingleItem() {
      list.add(1);

      list.deleteAll();

      assertTrue(list.isEmpty());
    }

    @Test
    void shouldDeleteAllWhenListContainsMultipleItems() {
      list.add(1);
      list.add(2);
      list.add(3);

      list.deleteAll();

      assertTrue(list.isEmpty());
    }
  }

  @Nested
  class WhenIsEmpty {

    @Test
    void shouldReturnTrueWhenListIsEmpty() {
      assertTrue(list.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenListContainsSingleItem() {
      list.add(1);

      assertFalse(list.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenListContainsMultipleItems() {
      list.add(1);
      list.add(1);
      list.add(1);

      assertFalse(list.isEmpty());
    }
  }

  @Nested
  class WhenIterate {

    @Test
    void shouldIterateEmptyList() {
      Iterator<Integer> iterator = list.iterator();

      assertFalse(iterator.hasNext());
    }

    @Test
    void shouldIterateListWithSingleItem() {
      list.add(1);

      Iterator<Integer> iterator = list.iterator();

      assertTrue(iterator.hasNext());
      assertEquals(1, iterator.next());
      assertFalse(iterator.hasNext());
    }

    @Test
    void shouldIterateListWithMultipleItems() {
      list.add(1);
      list.add(2);
      list.add(3);

      Iterator<Integer> iterator = list.iterator();

      assertTrue(iterator.hasNext());
      assertEquals(1, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(2, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(3, iterator.next());
      assertFalse(iterator.hasNext());
    }
  }
}
