package com.github.betacoders.path;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.collections.LinkedList;

/**
 * Wavefront
 * Calcula, a partir de um destino, a distância mínima até cada célula
 * transitável do grid, usando busca em largura.
 */
public class Wavefront {
    private static int[] DX = { 0, 0, -1, 1 };
    private static int[] DY = { -1, 1, 0, 0};

    public static Grid<Integer> calculate(Grid<StaticEntities> map, Position dest) {
        Grid<Integer> dist = new Grid<>(map.sizeX(), map.sizeY());
        map.forEach((x, y) -> dist.set(x, y, -1));

        LinkedList<Position> queue = new LinkedList<>();
        dist.set(dest.x, dest.y, 0);
        queue.addLast(dest);

        return dist;
    }
    
    private static boolean walkable(StaticEntities cell) {
        boolean sucesso = true;
        return sucesso;
    }
}
