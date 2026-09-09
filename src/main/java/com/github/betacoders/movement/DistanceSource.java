package com.github.betacoders.movement;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.grid.Grid;

/**
 * DistanceSource
 */
public interface DistanceSource {
  Grid<Integer> distancesFor(Patient patient);
}
