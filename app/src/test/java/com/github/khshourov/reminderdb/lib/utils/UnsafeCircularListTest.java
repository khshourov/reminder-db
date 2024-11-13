package com.github.khshourov.reminderdb.lib.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UnsafeCircularListTest {
  UnsafeCircularList<Integer> list;

  @BeforeEach
  void init() {
    list = new UnsafeCircularList<>();
  }

  @Nested
  class WhenAddingNode {

    @Test
    void anItemShouldBeAddedWhenListIsEmpty() {
      UnsafeNode<Integer> itemNode = list.add(1);

      assertEquals(1, itemNode.item);
      // Because it's circular, and we're keeping an anchor
      // node, both `prev` and `next` will point to that node
      assertNotNull(itemNode.prev);
      assertEquals(itemNode.prev, itemNode.next);
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
      // and itemNode1.prev --> anchor node <-- itemNode2.next
      assertNotNull(itemNode1.prev);
      assertEquals(itemNode1.prev, itemNode2.next);
    }
  }

  @Nested
  class WhenDeletingNode {

    @Test
    void falseShouldReturnWhenDeletableNodeIsNull() {
      boolean deleted = list.delete(null);

      assertFalse(deleted);
    }

    @Test
    void listShouldBeEmptyWhenTheSingleNodeIsDeleted() {
      UnsafeNode<Integer> itemNode = list.add(1);

      boolean deleted = list.delete(itemNode);

      assertTrue(deleted);
      // Make sure the node's references are null.
      // Otherwise, those instances won't be GC.
      assertNull(itemNode.prev);
      assertNull(itemNode.next);
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
