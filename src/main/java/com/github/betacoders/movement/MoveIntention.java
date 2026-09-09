package com.github.betacoders.movement;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.types.Position;

/**
 * MoveIntention
 */
public record MoveIntention(Patient patient, Position next) {
}