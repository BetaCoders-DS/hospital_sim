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
    private static final int[] DX = { 0, 0, -1, 1 };
    private static final int[] DY = { -1, 1, 0, 0};

    private static boolean walkable(StaticEntities cell) {
        boolean sucesso = true;
        if (cell instanceof StaticEntities.Wall) {
            sucesso = false;
        }
        if (cell instanceof StaticEntities.Nurse) {
            sucesso = false;
        }
        if (cell instanceof StaticEntities.Medic) {
            sucesso = false;
        }
        return sucesso;
    }

    public static Grid<Integer> calculate(Grid<StaticEntities> map, Position dest) {
        Grid<Integer> dist = new Grid<>(map.sizeX(), map.sizeY());
        map.forEach((x, y) -> dist.set(x, y, -1));

        LinkedList<Position> queue = new LinkedList<>();
        dist.set(dest.x, dest.y, 0);
        queue.addLast(dest);

        while(!queue.isEmpty()) {
            Position cur = queue.removeFirst();
            int curDist = dist.get(cur.x, cur.y);

            for(int i = 0; i < DX.length; ++i) {
                int nx = cur.x + DX[i];
                int ny = cur.y + DY[i];

                if (nx < 0 || nx >= map.sizeX() || ny < 0 || ny >= map.sizeY()) continue;
                if (!walkable(map.get(nx, ny))) continue;
                if (dist.get(nx, ny) != -1) continue;

                dist.set(nx, ny, curDist + 1);
                queue.addLast(new Position(nx, ny));
            }
        }
        return dist;
    }
}
