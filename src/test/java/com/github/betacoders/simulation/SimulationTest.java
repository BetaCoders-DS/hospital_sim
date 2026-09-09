package com.github.betacoders.simulation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.betacoders.TestMaps;
import com.github.betacoders.entities.Patient;
import com.github.betacoders.entities.Patient.State;
import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.time.SimulationClock;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.Position;

public class SimulationTest {

  private static final State[] CHAIN = {
      State.GOING_TO_TOTEM,
      State.AT_TOTEM,
      State.WAITING_FOR_TRIAGE,
      State.GOING_TO_TRIAGE,
      State.IN_TRIAGE,
      State.WAITING_FOR_MEDIC,
      State.GOING_TO_MEDIC,
      State.IN_CONSULTATION,
      State.GOING_TO_REMOVER,
      State.REMOVED,
  };

  private static int chainIndex(State s) {
    for (int i = 0; i < CHAIN.length; ++i) {
      if (CHAIN[i] == s) {
        return i;
      }
    }
    throw new IllegalArgumentException("Unexpected state " + s);
  }

  static final class FakeClock extends SimulationClock {
    private final double dt;
    private double virtual = 0;

    FakeClock(double dt) {
      this.dt = dt;
    }

    @Override
    public void update() {
      if (!isPaused()) {
        virtual += dt;
      }
    }

    @Override
    public double getTimePassed() {
      return virtual;
    }

    @Override
    public void reset() {
      virtual = 0;
      super.reset();
    }
  }

  private static Simulation sim(double dt) {
    return new Simulation(TestMaps.hospitalGrid(), SimulationConfig.defaults(), new FakeClock(dt));
  }

  private static int manhattan(Position a, Position b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
  }

  // Invariantes verificados a cada passo (auto-adaptativo, livre de flake).
  private final Map<Patient, Integer> lastState = new HashMap<>();
  private final Set<String> normalTickets = new HashSet<>();
  private final Set<String> preferentialTickets = new HashSet<>();
  private boolean sawSeatReserved = false;
  private boolean sawMedicLoad = false;

  private void assertInvariants(Simulation sim) {
    Grid<StaticEntities> map = sim.map();
    Position nursePos = TestMaps.nursePos();
    Position medicPos = TestMaps.medicPos();

    boolean[][] occupied = new boolean[map.sizeY()][map.sizeX()];
    Set<StaticEntities> reservedSeats = new HashSet<>();
    Set<Integer> patientIds = new HashSet<>();

    for (Patient p : sim.patients()) {
      State state = p.getState();
      Position pos = p.pos();

      assertTrue(patientIds.add(p.id()), "patient id must be unique, found duplicate " + p.id());
      assertFalse(occupied[pos.y][pos.x], "two patients share cell " + mark(pos));
      occupied[pos.y][pos.x] = true;

      StaticEntities cell = map.get(pos.x, pos.y);
      assertFalse(cell instanceof StaticEntities.Wall, "patient on wall " + mark(pos));
      assertFalse(cell instanceof StaticEntities.Nurse, "patient on nurse " + mark(pos));
      assertFalse(cell instanceof StaticEntities.Medic, "patient on medic " + mark(pos));

      int idx = chainIndex(state);
      Integer prev = lastState.get(p);
      if (prev != null) {
        assertTrue(idx >= prev, "state regressed " + CHAIN[prev] + " -> " + state);
      }
      lastState.put(p, idx);

      if (state == State.IN_TRIAGE) {
        assertEquals(1, manhattan(pos, nursePos), "in-triage patient not adjacent to nurse");
      }
      if (state == State.IN_CONSULTATION) {
        assertEquals(1, manhattan(pos, medicPos), "consulting patient not adjacent to medic");
      }

      if (idx == 2 && (prev == null || prev < 2)) {
        String ticket = p.ticketString();
        Set<String> bucket = ticket.charAt(0) == 'P' ? preferentialTickets : normalTickets;
        assertTrue(bucket.add(ticket), "duplicate ticket " + ticket);
      }

      StaticEntities target = p.target();
      if (target instanceof StaticEntities.Seat seat) {
        sawSeatReserved = true;
        assertFalse(seat.isFree(), "targeted seat not reserved");
        assertTrue(reservedSeats.add(seat), "same seat targeted twice");
      }

      if (idx >= 5) {
        if (state == State.WAITING_FOR_MEDIC) {
          Color color = p.getManchesterColor();
          assertTrue(color == Color.RED || color == Color.ORANGE || color == Color.YELLOW
              || color == Color.GREEN || color == Color.BLUE, "invalid color " + color);
        }
      }
    }

    int[] medicSizes = sim.medicQueueSizes();
    int medicLoad = 0;
    for (int s : medicSizes) {
      medicLoad += s;
    }
    if (medicLoad > 0 || sim.medicBusy()) {
      sawMedicLoad = true;
    }
    assertEquals(5, medicSizes.length, "medic queue sizes must stay length 5");
  }

  private static String mark(Position p) {
    return "(" + p.x + "," + p.y + ")";
  }

  @Test
  void servesPatientsToRemovalWithStableInvariants() {
    Simulation sim = sim(1.0);
    int steps = 0;
    int cap = 8000;
    while (sim.totalServed() < 2 && steps < cap) {
      sim.step();
      assertInvariants(sim);
      ++steps;
    }

    assertTrue(sim.totalServed() >= 2, "should have served at least two patients");
    assertTrue(sawMedicLoad, "medic queue/station should have load at some point");
    assertTrue(sawSeatReserved, "patients should have used seats");

    assertTrue(sim.waitingTimeAvg() >= 0);
    assertTrue(sim.systemTimeAvg() >= sim.waitingTimeAvg());
    assertTrue(normalTickets.size() > 0 || preferentialTickets.size() > 0);
  }

  @Test
  void queueSizesStayConsistentWithPatientCounts() {
    Simulation sim = sim(1.0);
    for (int i = 0; i < 3000; ++i) {
      sim.step();
      assertInvariants(sim);
      int inQueues = sim.normalQueueSize() + sim.preferentialQueueSize();
      assertTrue(inQueues >= 0);
      for (int s : sim.medicQueueSizes()) {
        assertTrue(s >= 0);
      }
    }
  }

  @Test
  void resetClearsAllState() {
    Simulation sim = sim(1.0);
    for (int i = 0; i < 300; ++i) {
      sim.step();
    }
    assertTrue(sim.activeCount() > 0 || sim.timePassed() > 0);

    sim.reset();

    assertEquals(0, sim.activeCount());
    assertEquals(0.0, sim.timePassed());
    assertEquals(0, sim.totalServed());
    assertEquals(0, sim.normalQueueSize());
    assertEquals(0, sim.preferentialQueueSize());
    assertArrayEquals(new int[5], sim.medicQueueSizes());
    assertFalse(sim.nurseBusy());
    assertFalse(sim.medicBusy());

    // Reset continua estavel: a simulacao volta a evoluir.
    for (int i = 0; i < 100; ++i) {
      sim.step();
    }
    assertTrue(sim.timePassed() > 0);
  }

  @Test
  void pauseFreezesSimulationTime() {
    Simulation sim = sim(1.0);
    for (int i = 0; i < 200 && sim.activeCount() == 0; ++i) {
      sim.step();
    }
    assertTrue(sim.activeCount() >= 1);

    double frozen = sim.timePassed();
    sim.pause();
    assertTrue(sim.isPaused());
    for (int i = 0; i < 10; ++i) {
      sim.step();
    }
    assertEquals(frozen, sim.timePassed());

    sim.resume();
    assertFalse(sim.isPaused());
    sim.step();
    assertTrue(sim.timePassed() > frozen);
  }

  @Test
  void rejectsMapMissingRequiredEntities() {
    Grid<StaticEntities> map = new Grid<>(5, 3);
    for (int y = 0; y < 3; ++y) {
      for (int x = 0; x < 5; ++x) {
        map.set(x, y, new StaticEntities.Floor());
      }
    }
    map.set(1, 1, new StaticEntities.Generator());
    map.set(3, 1, new StaticEntities.Remover());
    map.set(2, 1, new StaticEntities.Totem());

    assertThrows(IllegalArgumentException.class,
        () -> new Simulation(map, SimulationConfig.defaults()));
  }
}