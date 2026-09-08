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

    public ProcessingRenderer(PApplet app) {
        this.app = app;

        floorImage = app.loadImage("images/chao.png");
        seatImage = app.loadImage("images/seat.png");
        removerImage = app.loadImage("images/remover.png");
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

                drawCell(map.get(x, y), px, py, cellSize);
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
            app.image(floorImage, x, y, size, size);
        } else {
            app.fill(242);
            app.stroke(220);
            app.rect(x, y, size, size);
        }
    }

    private void drawSeat(float x, float y, float size) {
        if (seatImage != null) {
            app.image(seatImage, x, y, size, size);
        } else {
            app.fill(145);
            app.stroke(65);
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
            app.image(removerImage, x, y, size, size);
        } else {
            app.fill(190);
            app.stroke(45);
            app.rect(
                    x + size * 0.2f,
                    y + size * 0.12f,
                    size * 0.6f,
                    size * 0.76f
            );
        }
    }

    private void drawWall(float x, float y, float size) {
        app.fill(75);
        app.stroke(45);
        app.strokeWeight(Math.max(1, size * 0.025f));
        app.rect(x, y, size, size);

        app.fill(95);
        app.noStroke();
        app.rect(
                x + size * 0.12f,
                y + size * 0.12f,
                size * 0.76f,
                size * 0.16f
        );

        app.stroke(55);
        app.strokeWeight(Math.max(1, size * 0.025f));

        app.line(
                x + size * 0.08f,
                y + size * 0.48f,
                x + size * 0.92f,
                y + size * 0.48f
        );

        app.line(
                x + size * 0.08f,
                y + size * 0.8f,
                x + size * 0.92f,
                y + size * 0.8f
        );

        app.line(
                x + size * 0.32f,
                y + size * 0.12f,
                x + size * 0.32f,
                y + size * 0.48f
        );

        app.line(
                x + size * 0.72f,
                y + size * 0.48f,
                x + size * 0.72f,
                y + size * 0.8f
        );
    }

    private void drawGenerator(float x, float y, float size) {
        drawFloor(x, y, size);

        app.stroke(45);
        app.strokeWeight(Math.max(1, size * 0.04f));
        app.fill(180);

        app.rect(
                x + size * 0.2f,
                y + size * 0.16f,
                size * 0.6f,
                size * 0.68f,
                size * 0.08f
        );

        app.fill(70);
        app.rect(
                x + size * 0.32f,
                y + size * 0.32f,
                size * 0.36f,
                size * 0.28f
        );

        app.fill(80);
        app.circle(
                x + size * 0.5f,
                y + size * 0.72f,
                size * 0.12f
        );

        app.stroke(255);
        app.strokeWeight(Math.max(1, size * 0.025f));

        app.line(
                x + size * 0.4f,
                y + size * 0.43f,
                x + size * 0.6f,
                y + size * 0.43f
        );

        app.line(
                x + size * 0.5f,
                y + size * 0.35f,
                x + size * 0.5f,
                y + size * 0.51f
        );
    }

    private void drawTotem(float x, float y, float size) {
        drawFloor(x, y, size);

        app.stroke(45);
        app.strokeWeight(Math.max(1, size * 0.04f));
        app.fill(155);

        app.rect(
                x + size * 0.28f,
                y + size * 0.16f,
                size * 0.44f,
                size * 0.68f,
                size * 0.08f
        );

        app.fill(35);
        app.rect(
                x + size * 0.36f,
                y + size * 0.28f,
                size * 0.28f,
                size * 0.28f
        );

        app.fill(80);
        app.circle(
                x + size * 0.5f,
                y + size * 0.7f,
                size * 0.12f
        );

        app.stroke(220);
        app.strokeWeight(Math.max(1, size * 0.025f));

        app.line(
                x + size * 0.4f,
                y + size * 0.36f,
                x + size * 0.6f,
                y + size * 0.36f
        );

        app.line(
                x + size * 0.4f,
                y + size * 0.44f,
                x + size * 0.56f,
                y + size * 0.44f
        );
    }

    private void drawNurse(float x, float y, float size) {
        drawFloor(x, y, size);

        app.stroke(50);
        app.strokeWeight(Math.max(1, size * 0.025f));

        app.fill(210);
        app.rect(
                x + size * 0.16f,
                y + size * 0.48f,
                size * 0.68f,
                size * 0.32f,
                size * 0.08f
        );

        app.fill(225);
        app.rect(
                x + size * 0.28f,
                y + size * 0.24f,
                size * 0.44f,
                size * 0.32f,
                size * 0.08f
        );

        app.fill(70);
        app.rect(
                x + size * 0.36f,
                y + size * 0.32f,
                size * 0.28f,
                size * 0.12f
        );

        app.fill(240);
        app.rect(
                x + size * 0.46f,
                y + size * 0.28f,
                size * 0.08f,
                size * 0.2f
        );

        app.fill(45);
        app.circle(
                x + size * 0.36f,
                y + size * 0.68f,
                size * 0.08f
        );

        app.circle(
                x + size * 0.64f,
                y + size * 0.68f,
                size * 0.08f
        );
    }

    private void drawMedic(float x, float y, float size) {
        drawFloor(x, y, size);

        app.stroke(50);
        app.strokeWeight(Math.max(1, size * 0.025f));

        app.fill(210);
        app.rect(
                x + size * 0.16f,
                y + size * 0.44f,
                size * 0.68f,
                size * 0.36f,
                size * 0.08f
        );

        app.fill(225);
        app.rect(
                x + size * 0.28f,
                y + size * 0.2f,
                size * 0.44f,
                size * 0.32f,
                size * 0.08f
        );

        app.fill(45);
        app.rect(
                x + size * 0.32f,
                y + size * 0.28f,
                size * 0.36f,
                size * 0.08f
        );

        app.fill(240);
        app.rect(
                x + size * 0.46f,
                y + size * 0.24f,
                size * 0.08f,
                size * 0.2f
        );

        app.fill(45);
        app.circle(
                x + size * 0.36f,
                y + size * 0.66f,
                size * 0.08f
        );

        app.circle(
                x + size * 0.64f,
                y + size * 0.66f,
                size * 0.08f
        );
    }
}