package com.github.betacoders.path;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;

public class EntityFinder {
    public static Position findPosition(Grid<StaticEntities> map, StaticEntities target) {
        for (int x = 0; x < map.sizeX(); ++x) {
            for (int y = 0; y < map.sizeY(); ++y) {
                if (map.get(x, y) == target) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }
}
