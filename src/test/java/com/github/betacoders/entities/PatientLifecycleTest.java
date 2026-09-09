package com.github.betacoders.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Patient.State;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;
import com.github.betacoders.types.collections.ManchesterTree;

public class PatientLifecycleTest {

  private final Position pos = new Position(3, 4);

  public static Patient throughTriage(boolean preferential) {
    Vitals v = new Vitals(98, 37, 8, 1);
    Patient p = new Patient(new Position(1, 1), preferential, v);
    p.changeState(State.AT_TOTEM);
    p.giveTicketNum(4);
    p.changeState(State.GOING_TO_TRIAGE);
    p.changeState(State.IN_TRIAGE);
    p.completeTriage(new ManchesterTree());
    return p;
  }

  @Test
  void eachPatientGetsUniqueId() {
    Patient a = new Patient(pos, false);
    Patient b = new Patient(pos, true);
    Patient c = new Patient(pos, false);
    assertTrue(a.id() > 0);
    assertTrue(b.id() != a.id());
    assertTrue(c.id() != a.id());
    assertTrue(c.id() != b.id());
  }

  @Test
  void ticketRequiresTotemState() {
    Patient p = new Patient(pos, false);
    assertThrows(IllegalStateException.class, () -> p.giveTicketNum(1));
  }

  @Test
  void issuesNormalTicketAndTransitions() {
    Patient p = new Patient(pos, false);
    p.changeState(State.AT_TOTEM);
    p.giveTicketNum(7);
    assertEquals(State.WAITING_FOR_TRIAGE, p.getState());
    assertEquals("N0007", p.ticketString());
  }

  @Test
  void issuesPreferentialTicket() {
    Patient p = new Patient(pos, true);
    p.changeState(State.AT_TOTEM);
    p.giveTicketNum(3);
    assertEquals("P0003", p.ticketString());
  }

  @Test
  void rejectsSecondTicket() {
    Patient p = new Patient(pos, false);
    p.changeState(State.AT_TOTEM);
    p.giveTicketNum(1);
    assertThrows(Patient.TicketAlreadyGivenException.class, () -> p.giveTicketNum(2));
  }

  @Test
  void rejectsInvalidTicketNumbers() {
    Patient p = new Patient(pos, false);
    p.changeState(State.AT_TOTEM);
    assertThrows(Patient.InvalidTicketNumberException.class, () -> p.giveTicketNum(0));
    assertThrows(Patient.InvalidTicketNumberException.class, () -> p.giveTicketNum(-5));
  }

  @Test
  void ticketStringUnavailableUntilIssued() {
    Patient p = new Patient(pos, false);
    assertThrows(Patient.InvalidTicketNumberException.class, () -> p.ticketString());
  }

  @Test
  void waitingForTriageRequiresTicketPresent() {
    Patient p = new Patient(pos, false);
    p.changeState(State.AT_TOTEM);
    assertThrows(IllegalStateException.class, () -> p.changeState(State.WAITING_FOR_TRIAGE));
  }

  @Test
  void invalidStateTransitionsAreRejected() {
    Patient p = new Patient(pos, false);
    assertThrows(IllegalStateException.class, () -> p.changeState(State.IN_TRIAGE));
    assertThrows(IllegalStateException.class, () -> p.changeState(State.REMOVED));
  }

  @Test
  void completeTriageRequiresVitalsAndEntersWaitingForMedic() {
    Patient p = new Patient(pos, false);
    p.changeState(State.AT_TOTEM);
    p.giveTicketNum(1);
    p.changeState(State.GOING_TO_TRIAGE);
    p.changeState(State.IN_TRIAGE);
    assertThrows(IllegalStateException.class, () -> p.completeTriage(new ManchesterTree()));
    p.vitals(new Vitals(90, 38, 9, 1));
    p.completeTriage(new ManchesterTree());
    assertEquals(State.WAITING_FOR_MEDIC, p.getState());
    assertNotNull(p.getManchesterColor());
  }

  @Test
  void fullLifecycleToRemoved() {
    Patient p = throughTriage(false);
    p.changeState(State.GOING_TO_MEDIC);
    p.changeState(State.IN_CONSULTATION);
    p.completeConsultation();
    assertEquals(State.GOING_TO_REMOVER, p.getState());
    p.arriveAtRemover();
    assertEquals(State.REMOVED, p.getState());
  }

  @Test
  void targetPositionCacheResets() {
    Patient p = new Patient(pos, false);
    p.target(new StaticEntities.Totem());
    p.targetPosition(new Position(9, 9));
    Position cached = p.targetPosition();
    assertEquals(9, cached.x);
    assertEquals(9, cached.y);
  }
}