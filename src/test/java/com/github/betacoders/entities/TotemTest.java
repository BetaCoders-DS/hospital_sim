package com.github.betacoders.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Patient.State;
import com.github.betacoders.types.Position;

public class TotemTest {

  private static Patient atTotem(boolean preferential) {
    Patient p = new Patient(new Position(5, 5), preferential);
    p.changeState(State.AT_TOTEM);
    return p;
  }

  @Test
  void issuesSequentialTickets() {
    StaticEntities.Totem t = new StaticEntities.Totem();
    Patient p1 = atTotem(true);
    Patient p2 = atTotem(true);
    Patient n1 = atTotem(false);
    t.issueTicket(p1);
    t.issueTicket(p2);
    t.issueTicket(n1);
    assertEquals("P0001", p1.ticketString());
    assertEquals("P0002", p2.ticketString());
    assertEquals("N0001", n1.ticketString());
  }

  @Test
  void resetRestartsCounters() {
    StaticEntities.Totem t = new StaticEntities.Totem();
    t.issueTicket(atTotem(true));
    t.reset();
    Patient p1 = atTotem(true);
    t.issueTicket(p1);
    assertEquals("P0001", p1.ticketString());
  }

  @Test
  void rejectsPatientNotAtTotem() {
    StaticEntities.Totem t = new StaticEntities.Totem();
    Patient p = new Patient(new Position(0, 0), false);
    assertThrows(IllegalStateException.class, () -> t.issueTicket(p));
  }

  @Test
  void rejectsNullPatient() {
    StaticEntities.Totem t = new StaticEntities.Totem();
    assertThrows(IllegalArgumentException.class, () -> t.issueTicket(null));
  }

  @Test
  void rejectsReIssuedPatient() {
    StaticEntities.Totem t = new StaticEntities.Totem();
    Patient p = atTotem(false);
    t.issueTicket(p);
    assertThrows(Patient.TicketAlreadyGivenException.class, () -> t.issueTicket(p));
  }
}