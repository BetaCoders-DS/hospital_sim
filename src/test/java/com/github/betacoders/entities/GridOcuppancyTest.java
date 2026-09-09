package com.github.betacoders.entities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.betacoders.types.Position;

public class GridOcuppancyTest {

  private final GridOcuppancy grid = new GridOcuppancy(4, 4);

  @Test
  void startsEmpty() {
    Position p = new Position(2, 2);
    assertTrue(grid.isFree(p));
    assertNull(grid.pacientOn(p));
  }

  @Test
  void occupyMarksCellAndIsFreeClearsIt() {
    Patient p = new Patient(new Position(0, 0), false);
    Position cell = new Position(1, 1);
    grid.occupy(cell, p);
    assertFalse(grid.isFree(cell));
    assertSame(p, grid.pacientOn(cell));
    grid.free(cell);
    assertTrue(grid.isFree(cell));
    assertNull(grid.pacientOn(cell));
  }

  @Test
  void moveTransfersPatientBetweenCells() {
    Patient p = new Patient(new Position(0, 0), false);
    Position from = new Position(1, 1);
    Position to = new Position(1, 2);
    grid.occupy(from, p);
    grid.move(from, to, p);
    assertNull(grid.pacientOn(from));
    assertSame(p, grid.pacientOn(to));
  }

  @Test
  void cleanEmptiesGrid() {
    Patient p = new Patient(new Position(0, 0), false);
    grid.occupy(new Position(0, 0), p);
    grid.occupy(new Position(3, 3), new Patient(new Position(0, 0), true));
    grid.clean();
    for (int y = 0; y < 4; ++y) {
      for (int x = 0; x < 4; ++x) {
        assertTrue(grid.isFree(new Position(x, y)));
      }
    }
  }
}