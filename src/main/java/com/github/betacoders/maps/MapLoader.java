package com.github.betacoders.maps;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;

public class MapLoader {

    private final PApplet app;
    private Grid<StaticEntities> map;
    private int generators;
    private int removers;

    public MapLoader(PApplet app) {
        this.app = app;
    }

    public boolean load(String file) {
        String[] lines = app.loadStrings(file);
        if (lines == null || lines.length == 0) {
            return false;
        }

        String[] dimensions = PApplet.splitTokens(lines[0].trim());

        if (dimensions.length != 2) {
            return false;
        }

        int mapRows;
        int mapColumns;

        try {
            mapRows = Integer.parseInt(dimensions[0]);
            mapColumns = Integer.parseInt(dimensions[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (mapRows <= 0 || mapColumns <= 0) {
            return false;
        }
        if (lines.length != mapRows + 1) {
            return false;
        }
        map = new Grid<>(mapColumns, mapRows);

        generators = 0;
        removers = 0;

        for (int y = 0; y < mapRows; y++) {
            String line = lines[y + 1];
            if (line.length() != mapColumns) {
                return false;
            }
            for (int x = 0; x < mapColumns; x++) {
                char symbol = line.charAt(x);
                StaticEntities entity = createEntity(symbol);
                if (entity == null) {
                    return false;
                }
                map.set(x, y, entity);
                if (symbol == 'G') {
                    generators++;
                }
                if (symbol == 'R') {
                    removers++;
                }
            }
        }
        return true;
    }

    private StaticEntities createEntity(char symbol) {
        switch (symbol) {
            case '.':
                return new StaticEntities.Floor();
            case '#':
                return new StaticEntities.Wall();
            case 'G':
                return new StaticEntities.Generator();
            case 'R':
                return new StaticEntities.Remover();
            case 'T':
                return new StaticEntities.Totem();
            case 'A':
                return new StaticEntities.Seat();
            case 'E':
                return new StaticEntities.Nurse();
            case 'M':
                return new StaticEntities.Medic();
            default:
                return null;
        }
    }

    public boolean isValid() {
        if (map == null) {
            return false;
        }
        if (generators != 1 || removers != 1) {
            return false;
        }
        if (!hasClosedBorders()) {
            return false;
        }
        int[] generator = find(StaticEntities.Generator.class);
        int[] remover = find(StaticEntities.Remover.class);

        if (generator == null || remover == null) {
            return false;
        }
        return hasPath(
                generator[0],
                generator[1],
                remover[0],
                remover[1]);
    }

    private boolean hasClosedBorders() {
        int width = map.sizeX();
        int height = map.sizeY();
        for (int x = 0; x < width; x++) {
            if (!(map.get(x, 0) instanceof StaticEntities.Wall)) {
                return false;
            }
            if (!(map.get(x, height - 1) instanceof StaticEntities.Wall)) {
                return false;
            }
        }
        for (int y = 0; y < height; y++) {
            if (!(map.get(0, y) instanceof StaticEntities.Wall)) {
                return false;
            }
            if (!(map.get(width - 1, y) instanceof StaticEntities.Wall)) {
                return false;
            }
        }
        return true;
    }

    private int[] find(Class<?> type) {
        for (int y = 0; y < map.sizeY(); y++) {
            for (int x = 0; x < map.sizeX(); x++) {
                StaticEntities entity = map.get(x, y);

                if (type.isInstance(entity)) {
                    return new int[] { x, y };
                }
            }
        }
        return null;
    }

    private boolean hasPath(
            int startX,
            int startY,
            int targetX,
            int targetY) {

        boolean[][] visited = new boolean[map.sizeY()][map.sizeX()];

        int capacity = map.sizeX() * map.sizeY();
        int[] queueX = new int[capacity];
        int[] queueY = new int[capacity];

        int front = 0;
        int back = 0;

        queueX[back] = startX;
        queueY[back] = startY;
        back++;
        visited[startY][startX] = true;

        int[] dx = { -1, 1, 0, 0 };
        int[] dy = { 0, 0, -1, 1 };

        while (front < back) {
            int x = queueX[front];
            int y = queueY[front];

            front++;

            if (x == targetX && y == targetY) {
                return true;
            }
            for (int i = 0; i < 4; i++) {
                int nextX = x + dx[i];
                int nextY = y + dy[i];

                if (!isInsideMap(nextX, nextY)) {
                    continue;
                }
                if (visited[nextY][nextX]) {
                    continue;
                }
                if (map.get(nextX, nextY) instanceof StaticEntities.Wall) {
                    continue;
                }
                visited[nextY][nextX] = true;
                queueX[back] = nextX;
                queueY[back] = nextY;
                back++;
            }
        }
        return false;
    }

    private boolean isInsideMap(int x, int y) {
        return x >= 0
                && x < map.sizeX()
                && y >= 0
                && y < map.sizeY();
    }
    public Grid<StaticEntities> getMap() {
        return map;
    }
    public int getGenerators() {
        return generators;
    }
    public int getRemovers() {
        return removers;
    }
}