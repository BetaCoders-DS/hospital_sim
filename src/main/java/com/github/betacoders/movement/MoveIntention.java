package com.github.betacoders.movement;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.types.Position;

/**
 * MoveIntention
 */
public record MoveIntention(Pacient pacient, Position next) {
}