package com.github.betacoders.render;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;

public class ProcessingRenderer implements RenderI {

    private final PApplet app;
    private final int cellSize = 25;

    public ProcessingRenderer(PApplet app) {
        this.app = app;
    }

    @Override
    public void renderMap(Grid<StaticEntities> map) {
        app.background(255);

        if (map == null) {
            return;
        }

        for (int y = 0; y < map.sizeY(); y++) {
            for (int x = 0; x < map.sizeX(); x++) {
                drawCell(map.get(x, y), x, y);
            }
        }
    }

    private void drawCell(StaticEntities entity, int x, int y) {
        int pixelX = x * cellSize;
        int pixelY = y * cellSize;

        if (entity instanceof StaticEntities.Wall) {
            app.fill(60);
        } else {
            app.fill(230);
        }

        app.stroke(180);
        app.rect(pixelX, pixelY, cellSize, cellSize);
    }
}