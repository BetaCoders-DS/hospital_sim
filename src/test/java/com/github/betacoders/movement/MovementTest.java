package com.github.betacoders.movement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.GridOcuppancy;
import com.github.betacoders.entities.Patient;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.collections.LinkedList;

public class MovementTest {

  // Grade de distancias 4x4; todas as celulas com 9, exceto as marcadas.
  private static Grid<Integer> distGrid(Position... targets) {
    Grid<Integer> g = new Grid<>(4, 4);
    for (int y = 0; y < 4; ++y) {
      for (int x = 0; x < 4; ++x) {
        g.set(x, y, 9);
      }
    }
    for (Position p : targets) {
      g.set(p.x, p.y, 1);
    }
    return g;
  }

  private static final class FakeDist implements DistanceSource {
    private final Grid<Integer> grid;

    FakeDist(Grid<Integer> grid) {
      this.grid = grid;
    }

    @Override
    public Grid<Integer> distancesFor(Patient patient) {
      return grid;
    }
  }

  @Test
  void stepsTowardBestFreeNeighbor() {
    GridOcuppancy occ = new GridOcuppancy(4, 4);
    Patient p = new Patient(new Position(1, 1), false);
    occ.occupy(p.pos(), p);

    Patient blocker = new Patient(new Position(2, 1), false);
    occ.occupy(blocker.pos(), blocker);

    Grid<Integer> dist = distGrid(new Position(2, 1));
    LinkedList<Patient> patients = new LinkedList<>();
    patients.addLast(p);

    LinkedList<MoveIntention> intentions = Movement.computeIntentions(patients, new FakeDist(dist), occ);
    Position next = intentions.peekFirst().next();
    assertNotNull(next);
    assertTrue(next.x != 2 || next.y != 1, "must avoid the occupied best neighbor");

    Movement.resolve(intentions, occ);
    assertSame(p, occ.pacientOn(next));
    assertEqualsPos(p.pos(), next);
  }

  @Test
  void staysPutWhenEveryNeighborOccupied() {
    GridOcuppancy occ = new GridOcuppancy(4, 4);
    Patient p = new Patient(new Position(1, 1), false);
    occ.occupy(p.pos(), p);
    Position[] neighbors = {
        new Position(0, 1), new Position(2, 1), new Position(1, 0), new Position(1, 2)
    };
    for (Position n : neighbors) {
      occ.occupy(n, new Patient(n, false));
    }

    LinkedList<Patient> patients = new LinkedList<>();
    patients.addLast(p);
    LinkedList<MoveIntention> intentions = Movement.computeIntentions(patients, new FakeDist(distGrid()), occ);
    assertNull(intentions.peekFirst().next());
    Movement.resolve(intentions, occ);
    assertEqualsPos(p.pos(), new Position(1, 1));
  }

  @Test
  void onlyFirstClaimMovesWhenSharedTarget() {
    GridOcuppancy occ = new GridOcuppancy(4, 4);
    Patient p1 = new Patient(new Position(2, 1), false);
    Patient p2 = new Patient(new Position(2, 3), false);
    occ.occupy(p1.pos(), p1);
    occ.occupy(p2.pos(), p2);

    Grid<Integer> dist = distGrid(new Position(2, 2));
    LinkedList<Patient> patients = new LinkedList<>();
    patients.addLast(p1);
    patients.addLast(p2);

    LinkedList<MoveIntention> intentions = Movement.computeIntentions(patients, new FakeDist(dist), occ);
    Movement.resolve(intentions, occ);
    assertEqualsPos(p1.pos(), new Position(2, 2));
    assertEqualsPos(p2.pos(), new Position(2, 3));
    assertSame(p1, occ.pacientOn(new Position(2, 2)));
    assertSame(p2, occ.pacientOn(new Position(2, 3)));
  }

  private static void assertEqualsPos(Position a, Position b) {
    org.junit.jupiter.api.Assertions.assertEquals(a.x, b.x);
    org.junit.jupiter.api.Assertions.assertEquals(a.y, b.y);
  }
}