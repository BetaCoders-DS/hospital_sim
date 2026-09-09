package com.github.betacoders.simulation;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.types.Position;

public record SeatSpot(StaticEntities.Seat seat, Position pos) {
}