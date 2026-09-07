package com.github.movement;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.grid.Grid;

/**
 * DistanceSource
 * Ponte pro problema alvo -> posição, que ainda não foi resolvido.
 */
public interface DistanceSource {
  Grid<Integer> distancesFor(Pacient pacient);
}