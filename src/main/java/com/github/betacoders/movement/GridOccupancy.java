package com.github.betacoders.movement;

import com.github.betacoders.entities.GridOcupacao;
import com.github.betacoders.entities.Pacient;
import com.github.betacoders.types.Position;

public class GridOccupancy implements Occupancy {
    private final GridOcupacao grid;

    public GridOccupancy(GridOcupacao grid) {
        this.grid = grid;
    }

    @Override
    public boolean isFree(Position pos) {
        return grid.estaLivre(pos);
    }

    @Override
    public void move(Position origem, Position dest, Pacient alves) {
        grid.mover(origem, dest, alves);
    }
}
