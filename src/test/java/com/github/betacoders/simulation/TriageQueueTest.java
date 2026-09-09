package com.github.betacoders.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.types.Position;

public class TriageQueueTest {

  private static Patient waiting(boolean preferential) {
    Patient p = new Patient(new Position(0, 0), preferential);
    p.changeState(Patient.State.AT_TOTEM);
    p.giveTicketNum(1);
    return p;
  }

  @Test
  void alternatesTwoPrefByOneNormal() {
    TriageQueue q = new TriageQueue();
    Patient n = waiting(false);
    Patient p1 = waiting(true);
    Patient p2 = waiting(true);
    Patient p3 = waiting(true);
    Patient n2 = waiting(false);
    q.enqueue(n);
    q.enqueue(p1);
    q.enqueue(p2);
    q.enqueue(p3);
    q.enqueue(n2);

    assertSame(p1, q.dequeueNext());
    assertSame(p2, q.dequeueNext());
    assertSame(n, q.dequeueNext());
    assertSame(p3, q.dequeueNext());
    assertSame(n2, q.dequeueNext());
    assertNull(q.dequeueNext());
  }

  @Test
  void servesPreferentialWhenOnlyPrefPending() {
    TriageQueue q = new TriageQueue();
    Patient p1 = waiting(true);
    Patient p2 = waiting(true);
    Patient p3 = waiting(true);
    q.enqueue(p1);
    q.enqueue(p2);
    q.enqueue(p3);

    assertSame(p1, q.dequeueNext());
    assertSame(p2, q.dequeueNext());
    assertSame(p3, q.dequeueNext());
  }

  @Test
  void clearsQueuesAndResetsCounter() {
    TriageQueue q = new TriageQueue();
    q.enqueue(waiting(true));
    q.enqueue(waiting(true));
    q.enqueue(waiting(false));
    q.clear();

    Patient p = waiting(true);
    Patient n = waiting(false);
    q.enqueue(p);
    q.enqueue(n);
    assertSame(p, q.dequeueNext());
    assertSame(n, q.dequeueNext());
    assertEquals(0, q.normalSize());
    assertEquals(0, q.preferentialSize());
  }

  @Test
  void rejectsPatientNotWaitingForTriage() {
    TriageQueue q = new TriageQueue();
    Patient p = new Patient(new Position(0, 0), false);
    assertThrows(IllegalStateException.class, () -> q.enqueue(p));
  }

  @Test
  void rejectsDuplicateEnqueue() {
    TriageQueue q = new TriageQueue();
    Patient p = waiting(false);
    q.enqueue(p);
    assertThrows(IllegalStateException.class, () -> q.enqueue(p));
  }

  @Test
  void reportsQueueSizes() {
    TriageQueue q = new TriageQueue();
    q.enqueue(waiting(false));
    q.enqueue(waiting(false));
    q.enqueue(waiting(true));
    assertEquals(2, q.normalSize());
    assertEquals(1, q.preferentialSize());
  }
}