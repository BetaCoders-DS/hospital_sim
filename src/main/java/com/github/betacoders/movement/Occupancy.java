package com.github.betacoders.movement;

import com.github.betacoders.types.Position;
import com.github.betacoders.entities.Pacient;

public interface Occupancy {
  boolean isFree(Position pos);
  void move(Position origem, Position destino, Pacient p);
}
