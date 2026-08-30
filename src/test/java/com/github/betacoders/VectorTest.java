package com.github.betacoders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.betacoders.types.collections.Vector;

class VectorTest {

  private Vector<Integer> v;

  @BeforeEach
  void setUp() {
    v = new Vector<>();
  }

  @Nested
  class Construction {

    @Test
    void defaultIsEmpty() {
      assertTrue(v.isEmpty());
    }

    @Test
    void defaultSizeZero() {
      assertEquals(0, v.size());
    }

    @Test
    void defaultCapIsInitCap() {
      assertEquals(v.INIT_CAP, v.cap());
    }

    @Test
    void capConstructorSizeZero() {
      Vector<Integer> v2 = new Vector<>(10);
      assertEquals(0, v2.size());
    }

    @Test
    void capConstructorSetsCapField() {
      Vector<Integer> v2 = new Vector<>(10);
      assertEquals(10, v2.cap());
    }
  }

  @Nested
  class PushPop {

    @Test
    void pushIncreasesSize() {
      v.push(1);
      assertEquals(1, v.size());
    }

    @Test
    void pushThenGetReturnsValue() {
      v.push(42);
      assertEquals(42, v.get(0));
    }

    @Test
    void pushBeyondInitCapGrowsCap() {
      v.push(1);
      v.push(2);
      v.push(3);
      assertEquals(3, v.size());
      assertTrue(v.cap() >= 3);
    }

    @Test
    void pushManyPreservesOrder() {
      for (int i = 0; i < 50; i++) {
        v.push(i);
      }
      for (int i = 0; i < 50; i++) {
        assertEquals(i, v.get(i));
      }
    }

    @Test
    void popReturnsLastPushed() {
      v.push(1);
      v.push(2);
      assertEquals(2, v.pop());
    }

    @Test
    void popDecreasesSize() {
      v.push(1);
      v.push(2);
      v.pop();
      assertEquals(1, v.size());
    }

    @Test
    void popClearsSlot() {
      v.push(1);
      v.pop();
      assertNull(v.get(0));
    }

    @Test
    void popOnEmptyReturnsNull() {
      assertNull(v.pop());
    }

    @Test
    void popOnEmptyDoesNotChangeSize() {
      v.pop();
      assertEquals(0, v.size());
    }
  }

  @Nested
  class Peek {

    @Test
    void peekReturnsLastPushedWithoutRemoving() {
      v.push(1);
      v.push(2);
      assertEquals(2, v.peek());
      assertEquals(2, v.size());
    }

    @Test
    void peekOnEmptyReturnsNull() {
      assertNull(v.peek());
    }
  }

  @Nested
  class GetSet {

    @Test
    void setWithinCapStoresValue() {
      v.set(0, 7);
      assertEquals(7, v.get(0));
    }

    @Test
    void setUpdatesSizeToIndexPlusOne() {
      v.set(1, 5);
      assertEquals(2, v.size());
    }

    @Test
    void setBeyondCapThrows_currentBehavior() {
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> v.set(v.cap(), 1));
    }

    @Test
    void setAtLowerIndexAfterHigherKeepsMaxSize() {
      v.resize(10);
      v.set(3, 1);
      v.set(1, 2);
      assertEquals(4, v.size());
    }
  }

  @Nested
  class Resize {

    @Test
    void resizeUpKeepsExistingValues() {
      v.push(1);
      v.push(2);
      v.resize(10);
      assertEquals(1, v.get(0));
      assertEquals(2, v.get(1));
    }

    @Test
    void resizeUpDoesNotChangeSize() {
      v.push(1);
      v.resize(10);
      assertEquals(1, v.size());
    }

    @Test
    void resizeDownClampsSize() {
      v.push(1);
      v.push(2);
      v.push(3);
      v.resize(2);
      assertEquals(2, v.size());
    }

    @Test
    void resizeDownTruncatesArray() {
      v.push(1);
      v.push(2);
      v.resize(1);
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> v.get(1));
    }

    @Test
    void resizeUpdatesCapField() {
      v.resize(10);
      assertEquals(10, v.cap());
    }
  }

  @Nested
  class IsEmpty {

    @Test
    void trueOnNoPush() {
      assertTrue(v.isEmpty());
    }

    @Test
    void falseAfterPush() {
      v.push(1);
      assertFalse(v.isEmpty());
    }

    @Test
    void trueAfterPushAndPop() {
      v.push(1);
      v.pop();
      assertTrue(v.isEmpty());
    }
  }
}
