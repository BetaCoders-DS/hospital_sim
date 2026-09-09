package com.github.betacoders.render;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;
import processing.core.PImage;

public class ProcessingRenderer implements RenderI {

    private final PApplet app;

    private PImage floorImage;
    private PImage seatImage;
    private PImage removerImage;
    private PImage generatorImage;
    private PImage totemImage;
    private PImage nurseImage;
    private PImage medicImage;

    public ProcessingRenderer(PApplet app) {
        this.app = app;

        floorImage = app.loadImage("images/chao.png");
        seatImage = app.loadImage("images/seat.png");
        removerImage = app.loadImage("images/remover.png");
        generatorImage = app.loadImage("images/generator.png");
        totemImage = app.loadImage("images/totem.png");
        nurseImage = app.loadImage("images/nurse.png");
        medicImage = app.loadImage("images/medic.png");
    }

    @Override
    public void renderMap(Grid<StaticEntities> map) {
        app.background(235);

        float margin = 20;

        float availableWidth = app.width - margin * 2;
        float availableHeight = app.height - margin * 2;

        float cellWidth = availableWidth / map.sizeX();
        float cellHeight = availableHeight / map.sizeY();

        float cellSize = Math.min(cellWidth, cellHeight);

        float mapWidth = map.sizeX() * cellSize;
        float mapHeight = map.sizeY() * cellSize;

        float offsetX = (app.width - mapWidth) / 2;
        float offsetY = (app.height - mapHeight) / 2;

        for (int y = 0; y < map.sizeY(); y++) {
            for (int x = 0; x < map.sizeX(); x++) {

                float px = offsetX + x * cellSize;
                float py = offsetY + y * cellSize;

                drawCell(
                        map.get(x, y),
                        px,
                        py,
                        cellSize
                );
            }
        }
    }

    private void drawCell(
            StaticEntities entity,
            float x,
            float y,
            float size) {

        if (entity instanceof StaticEntities.Wall) {
            drawWall(x, y, size);

        } else if (entity instanceof StaticEntities.Generator) {
            drawGenerator(x, y, size);

        } else if (entity instanceof StaticEntities.Remover) {
            drawRemover(x, y, size);

        } else if (entity instanceof StaticEntities.Totem) {
            drawTotem(x, y, size);

        } else if (entity instanceof StaticEntities.Seat) {
            drawSeat(x, y, size);

        } else if (entity instanceof StaticEntities.Nurse) {
            drawNurse(x, y, size);

        } else if (entity instanceof StaticEntities.Medic) {
            drawMedic(x, y, size);

        } else {
            drawFloor(x, y, size);
        }
    }

    private void drawFloor(float x, float y, float size) {
        if (floorImage != null) {
            app.image(
                    floorImage,
                    x,
                    y,
                    size,
                    size
            );
        } else {
            app.fill(242);
            app.noStroke();
            app.rect(x, y, size, size);
        }
    }

    private void drawSeat(float x, float y, float size) {
        if (seatImage != null) {
            app.image(
                    seatImage,
                    x,
                    y,
                    size,
                    size
            );
        } else {
            app.fill(145);
            app.noStroke();

            app.rect(
                    x + size * 0.24f,
                    y + size * 0.24f,
                    size * 0.52f,
                    size * 0.32f
            );
        }
    }

    private void drawRemover(float x, float y, float size) {
        if (removerImage != null) {
            app.image(
                    removerImage,
                    x,
                    y,
                    size,
                    size
            );
        } else {
            app.fill(190);
            app.noStroke();

            app.rect(
                    x + size * 0.2f,
                    y + size * 0.12f,
                    size * 0.6f,
                    size * 0.76f
            );
        }
    }

    private void drawGenerator(float x, float y, float size) {
        drawFloor(x, y, size);

        if (generatorImage != null) {
            app.image(
                    generatorImage,
                    x,
                    y,
                    size,
                    size
            );
        }
    }

    private void drawTotem(float x, float y, float size) {
        drawFloor(x, y, size);

        if (totemImage != null) {
            app.image(
                    totemImage,
                    x,
                    y,
                    size,
                    size
            );
        }
    }

    private void drawNurse(float x, float y, float size) {
        drawFloor(x, y, size);

        if (nurseImage != null) {
            app.image(
                    nurseImage,
                    x,
                    y,
                    size,
                    size
            );
        }
    }

    private void drawMedic(float x, float y, float size) {
        drawFloor(x, y, size);

        if (medicImage != null) {
            app.image(
                    medicImage,
                    x,
                    y,
                    size,
                    size
            );
        }
    }

    private void drawWall(float x, float y, float size) {
        app.noStroke();
        app.fill(215, 220, 225);

        app.rect(
                x,
                y,
                size,
                size
        );
    }
}