package com.github.betacoders.movement;

import com.github.betacoders.entities.Patient;
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
    public Grid<Integer> distancesFor(Patient p) {
        Position dest = p.targetPosition();

        if (dest == null) {
            dest = EntityFinder.findPosition(map, p.target());
            p.targetPosition(dest);
        }

        if (dest == null) {
            return allUnreachable();
        }
        return Wavefront.calculate(map, dest);
    }

    private Grid<Integer> allUnreachable() {
        Grid<Integer> dist = new Grid<>(map.sizeX(), map.sizeY());
        for (int x = 0; x < map.sizeX(); ++x) {
            for (int y = 0; y < map.sizeY(); ++y) {
                dist.set(x, y, -1);
            }
        }
        return dist;
    }
}