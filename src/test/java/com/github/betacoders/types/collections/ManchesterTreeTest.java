package com.github.betacoders.types.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.betacoders.types.ManchesterNode.Color;

public class ManchesterTreeTest {

  private final ManchesterTree tree = new ManchesterTree();

  // Atributos: 0 = saturacao, 1 = temperatura, 2 = dor, 3 = consciencia.
  private static float[] v(float o2, float temp, float pain, float cons) {
    return new float[] { o2, temp, pain, cons };
  }

  @Test
  void classifiesRedWhenConscious() {
    assertEquals(Color.RED, tree.classify(v(98, 37, 5, 1)));
  }

  @Test
  void classifiesOrangeWhenO2Below92() {
    assertEquals(Color.ORANGE, tree.classify(v(90, 37, 5, 0)));
  }

  @Test
  void classifiesYellowWhenPainAtLeast8() {
    assertEquals(Color.YELLOW, tree.classify(v(95, 37, 9, 0)));
  }

  @Test
  void classifiesGreenWhenTempAtLeast38() {
    assertEquals(Color.GREEN, tree.classify(v(95, 39, 5, 0)));
  }

  @Test
  void classifiesBlueOtherwise() {
    assertEquals(Color.BLUE, tree.classify(v(95, 36, 5, 0)));
  }

  @Test
  void o2BoundaryIsNotOrange() {
    assertEquals(Color.BLUE, tree.classify(v(92, 36, 5, 0)));
  }

  @Test
  void painBoundaryIsYellow() {
    assertEquals(Color.YELLOW, tree.classify(v(95, 36, 8, 0)));
  }

  @Test
  void tempBoundaryIsGreen() {
    assertEquals(Color.GREEN, tree.classify(v(95, 38, 5, 0)));
  }

  @Test
  void rejectsInvalidAttributeCount() {
    assertThrows(IllegalArgumentException.class, () -> tree.classify(new float[] { 1 }));
    assertThrows(IllegalArgumentException.class, () -> tree.classify(new float[] { 1, 2, 3 }));
    assertThrows(IllegalArgumentException.class, () -> tree.classify(new float[] { 1, 2, 3, 4, 5 }));
  }

  @Test
  void rejectsNullAttributes() {
    assertThrows(IllegalArgumentException.class, () -> tree.classify(null));
  }

  @Test
  void rejectsNonFiniteAttributes() {
    assertThrows(IllegalArgumentException.class,
        () -> tree.classify(new float[] { Float.NaN, 37, 5, 0 }));
  }
}