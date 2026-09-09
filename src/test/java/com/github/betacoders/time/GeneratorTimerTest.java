package com.github.betacoders.time;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GeneratorTimerTest {

  @Test
  void spawnTimesAreNonNegativeFinite() {
    for (int i = 0; i < 5000; ++i) {
      double t = GeneratorTimer.nextSpawn(5.0);
      assertTrue(t >= 0);
      assertTrue(Double.isFinite(t));
    }
  }

  @Test
  void serviceTimesRespectMinimum() {
    for (int i = 0; i < 5000; ++i) {
      assertTrue(GeneratorTimer.serviceTime(6, 2, 2) >= 2);
      assertTrue(GeneratorTimer.serviceTime(12, 4, 4) >= 4);
    }
  }

  @Test
  void serviceTimesHoverAroundAverage() {
    double sum = 0;
    int n = 100_000;
    for (int i = 0; i < n; ++i) {
      sum += GeneratorTimer.serviceTime(12, 4, 4);
    }
    double avg = sum / n;
    assertTrue(avg > 11.0 && avg < 13.0,
        "average should stay near the configured mean, got " + avg);
  }
}