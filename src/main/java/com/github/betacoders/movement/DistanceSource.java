package com.github.betacoders.movement;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.grid.Grid;

/**
 * DistanceSource
 */
public interface DistanceSource {
  Grid<Integer> distancesFor(Pacient pacient);
}
