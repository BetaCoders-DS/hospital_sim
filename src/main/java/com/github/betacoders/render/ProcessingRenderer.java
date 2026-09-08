package com.github.betacoders.render;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;

public class ProcessingRenderer implements RenderI {

    private final PApplet app;
    private final int cellSize = 32;

    public ProcessingRenderer(PApplet app) {
        this.app = app;
    }

    @Override
    public void renderMap(Grid<StaticEntities> map) {
        app.background(35);

        if (map == null) {
            return;
        }

        int mapWidth = map.sizeX() * cellSize;
        int mapHeight = map.sizeY() * cellSize;

        int offsetX = (app.width - mapWidth) / 2;
        int offsetY = (app.height - mapHeight) / 2;

        for (int y = 0; y < map.sizeY(); y++) {
            for (int x = 0; x < map.sizeX(); x++) {
                drawCell(map.get(x, y), x, y, offsetX, offsetY);
            }
        }
    }

    private void drawCell(
            StaticEntities entity,
            int x,
            int y,
            int offsetX,
            int offsetY) {

        int pixelX = offsetX + x * cellSize;
        int pixelY = offsetY + y * cellSize;

        if (entity instanceof StaticEntities.Wall) {
            app.fill(55);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Generator) {
            app.fill(70, 150, 80);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Remover) {
            app.fill(170, 70, 70);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Totem) {
            app.fill(180, 150, 60);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Seat) {
            app.fill(80, 120, 170);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Nurse) {
            app.fill(180, 100, 180);
            app.stroke(30);
        } else if (entity instanceof StaticEntities.Medic) {
            app.fill(80, 180, 180);
            app.stroke(30);
        } else {
            app.fill(220);
            app.stroke(190);
        }

        app.rect(
                pixelX,
                pixelY,
                cellSize,
                cellSize
        );
    }
}