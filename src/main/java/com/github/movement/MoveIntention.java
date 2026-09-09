package com.github.movement;

import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.entities.Patient;


public record MoveIntention(Patient patient, Position next) {
    
}
