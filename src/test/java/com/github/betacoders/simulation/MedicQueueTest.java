package com.github.betacoders.simulation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;
import com.github.betacoders.types.collections.ManchesterTree;

public class MedicQueueTest {

  private static Patient classified(Color color) {
    Vitals v = switch (color) {
      case RED -> new Vitals(98, 37, 5, 1);
      case ORANGE -> new Vitals(90, 37, 5, 0);
      case YELLOW -> new Vitals(95, 37, 9, 0);
      case GREEN -> new Vitals(95, 39, 5, 0);
      case BLUE -> new Vitals(95, 36, 5, 0);
    };
    Patient p = new Patient(new Position(0, 0), false, v);
    p.changeState(Patient.State.AT_TOTEM);
    p.giveTicketNum(1);
    p.changeState(Patient.State.GOING_TO_TRIAGE);
    p.changeState(Patient.State.IN_TRIAGE);
    p.completeTriage(new ManchesterTree());
    return p;
  }

  @Test
  void dequeuesByStrictColorPriority() {
    MedicQueue q = new MedicQueue();
    Patient blue = classified(Color.BLUE);
    Patient red1 = classified(Color.RED);
    Patient yellow = classified(Color.YELLOW);
    Patient green = classified(Color.GREEN);
    Patient red2 = classified(Color.RED);
    q.enqueue(blue);
    q.enqueue(red1);
    q.enqueue(yellow);
    q.enqueue(green);
    q.enqueue(red2);

    assertSame(red1, q.dequeueNext());
    assertSame(red2, q.dequeueNext());
    assertSame(yellow, q.dequeueNext());
    assertSame(green, q.dequeueNext());
    assertSame(blue, q.dequeueNext());
    assertNull(q.dequeueNext());
  }

  @Test
  void keepsFifoWithinSameColor() {
    MedicQueue q = new MedicQueue();
    Patient red1 = classified(Color.RED);
    Patient red2 = classified(Color.RED);
    Patient red3 = classified(Color.RED);
    q.enqueue(red1);
    q.enqueue(red2);
    q.enqueue(red3);

    assertSame(red1, q.dequeueNext());
    assertSame(red2, q.dequeueNext());
    assertSame(red3, q.dequeueNext());
  }

  @Test
  void rejectsPatientNotWaitingForMedic() {
    MedicQueue q = new MedicQueue();
    Patient p = new Patient(new Position(0, 0), false);
    p.changeState(Patient.State.AT_TOTEM);
    p.giveTicketNum(1);
    assertThrows(IllegalStateException.class, () -> q.enqueue(p));
  }

  @Test
  void clearsQueueAndZeroesSizes() {
    MedicQueue q = new MedicQueue();
    q.enqueue(classified(Color.RED));
    q.enqueue(classified(Color.BLUE));
    q.clear();
    assertArrayEquals(new int[] { 0, 0, 0, 0, 0 }, q.sizes());
    assertNull(q.dequeueNext());
  }

  @Test
  void reportsPerColorSizes() {
    MedicQueue q = new MedicQueue();
    q.enqueue(classified(Color.RED));
    q.enqueue(classified(Color.RED));
    q.enqueue(classified(Color.YELLOW));
    q.enqueue(classified(Color.BLUE));
    assertArrayEquals(new int[] { 2, 0, 1, 0, 1 }, q.sizes());
  }
}