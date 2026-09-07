package com.github.movement;

import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.entities.Pacient;


public record MoveIntention(Pacient Pacient, Position next) {
    
}
