package com.github.betacoders.movement;

import com.github.betacoders.types.Position;
import com.github.betacoders.entities.Pacient;

public record MoveIntention(Pacient pacient, Position next) {
}
