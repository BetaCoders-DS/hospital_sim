package com.github.betacoders.movement;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.path.EntityFinder;
import com.github.betacoders.path.Wavefront;
import com.github.betacoders.types.Position;

public class MapDistanceSource implements DistanceSource {
    private final Grid<StaticEntities> map;

    public MapDistanceSource(Grid<StaticEntities> map) {
        this.map = map;
    }

    @Override
    public Grid<Integer> distancesFor(Pacient p) {
        Position dest = p.targetPosition();

        if (dest == null) {
            dest = EntityFinder.findPosition(map, p.target());
            p.targetPosition(dest);
        }
        return Wavefront.calculate(map, dest);
    }
}