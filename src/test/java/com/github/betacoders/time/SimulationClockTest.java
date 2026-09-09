package com.github.betacoders.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SimulationClockTest {

  @Test
  void advancesOnlyWhenNotPaused() {
    SimulationClock clock = new SimulationClock();
    clock.start();
    assertEquals(0.0, clock.getTimePassed());
    assertTrue(!clock.isPaused());

    double t0 = clock.getTimePassed();
    clock.update();
    assertTrue(clock.getTimePassed() >= t0);

    clock.pause();
    double frozen = clock.getTimePassed();
    for (int i = 0; i < 20; ++i) {
      clock.update();
    }
    assertEquals(frozen, clock.getTimePassed());

    clock.resume();
    clock.update();
    assertTrue(clock.getTimePassed() >= frozen);
  }

  @Test
  void resetRestartsFromZero() {
    SimulationClock clock = new SimulationClock();
    clock.start();
    clock.update();
    clock.reset();
    assertEquals(0.0, clock.getTimePassed());
    assertTrue(!clock.isPaused());
  }
}