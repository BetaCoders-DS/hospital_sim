package com.github.betacoders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.betacoders.types.collections.FlatBTree;

class FlatBTreeTest {

  private FlatBTree<Integer> tree;

  @BeforeEach
  void setUp() {
    tree = new FlatBTree<>();
  }

  @Nested
  class Construction {

    @Test
    void defaultConstructorIsEmpty() {
      assertTrue(tree.isEmpty());
    }

    @Test
    void defaultConstructorSizeZero() {
      assertEquals(0, tree.size());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 4, 16 })
    void capacityConstructorIsEmpty(int cap) {
      FlatBTree<Integer> t = new FlatBTree<>(cap);
      assertTrue(t.isEmpty());
      assertEquals(0, t.size());
    }
  }

  @Nested
  class Insert {

    @Test
    void insertIntoEmptyReturnsNull() {
      assertNull(tree.insert(5));
    }

    @Test
    void insertMakesTreeNonEmpty() {
      tree.insert(5);
      assertFalse(tree.isEmpty());
    }

    @Test
    void insertIncrementsSize() {
      tree.insert(10);
      tree.insert(5);
      tree.insert(20);
      assertEquals(3, tree.size());
    }

    @Test
    void insertSecondDistinctValueReturnsNull() {
      tree.insert(10);
      assertNull(tree.insert(5));
      assertNull(tree.insert(20));
    }

    @Test
    void insertDuplicateReturnsExistingValue() {
      tree.insert(10);
      Integer existing = tree.insert(10);
      assertEquals(10, existing);
    }

    @Test
    void insertDuplicateDoesNotIncrementSize() {
      tree.insert(10);
      tree.insert(10);
      assertEquals(1, tree.size());
    }

    @Test
    void insertDuplicateAmongManyReturnsExistingValue() {
      int[] values = { 10, 5, 20, 1, 7, 15, 25 };
      for (int v : values) {
        tree.insert(v);
      }
      assertEquals(15, tree.insert(15));
    }

    @Test
    void insertAscendingValues() {
      for (int i = 0; i < 20; i++) {
        assertNull(tree.insert(i));
      }
    }

    @Test
    void insertDescendingValues() {
      for (int i = 20; i >= 0; i--) {
        assertNull(tree.insert(i));
      }
    }
  }

  @Nested
  class Contains {

    @Test
    void containsFindsInsertedValue() {
      tree.insert(42);
      assertTrue(tree.contains(42));
    }

    @Test
    void containsOnEmptyTreeIsFalse() {
      assertFalse(tree.contains(1));
    }

    @Test
    void containsFalseForAbsentValue() {
      tree.insert(10);
      tree.insert(5);
      assertFalse(tree.contains(99));
    }

    @Test
    void containsFindsDeepValue() {
      tree.insert(42);
      tree.insert(38);
      tree.insert(67);
      tree.insert(1);
      tree.insert(5);
      tree.insert(83);
      assertTrue(tree.contains(5));
    }
  }

  @Nested
  class IteratorTests {

    @Test
    void iteratorIsStubbedNull() {
      tree.insert(1);
      Iterator<Integer> it = tree.iterator();
      assertNull(it);
    }
  }
}
