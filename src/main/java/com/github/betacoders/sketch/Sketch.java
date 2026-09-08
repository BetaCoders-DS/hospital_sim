package com.github.betacoders.sketch;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.maps.MapLoader;
import com.github.betacoders.render.ProcessingRenderer;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Sketch extends PApplet {

    private ProcessingRenderer renderer;
    private MapLoader mapLoader;
    private Grid<StaticEntities> map;
    private PImage menuBackground;
    private PFont titleFont;
    private PFont buttonFont;
    private PFont smallFont;

    private enum Screen {
        MENU,
        MAPS,
        SIMULATION,
        PAUSE
    }
    private Screen currentScreen = Screen.MENU;

    private final int buttonWidth = 300;
    private final int buttonHeight = 60;

    @Override
    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        renderer = new ProcessingRenderer(this);
        mapLoader = new MapLoader(this);

        menuBackground = loadImage("menu/hospital_menu.png");

        titleFont = createFont("SansSerif.bold", 42);
        buttonFont = createFont("SansSerif.bold", 25);
        smallFont = createFont("SansSerif", 18);
    }

    @Override
    public void draw() {
        if (currentScreen == Screen.MENU) {
            drawMenu();
        } else if (currentScreen == Screen.MAPS) {
            drawMaps();
        } else if (currentScreen == Screen.SIMULATION) {
            renderer.renderMap(map);
        } else if (currentScreen == Screen.PAUSE) {
            drawPause();
        }
    }

    private void drawMenu() {
        drawBackground();

        fill(0, 0, 0, 150);
        rect(0, 0, width, height);

        int panelWidth = 540;
        int panelHeight = 510;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        fill(10, 15, 20, 120);
        stroke(240);
        strokeWeight(2);
        rect(panelX, panelY, panelWidth, panelHeight, 18);

        stroke(170);
        strokeWeight(1);
        rect(
                panelX + 10,
                panelY + 10,
                panelWidth - 20,
                panelHeight - 20,
                14
        );

        textAlign(CENTER, CENTER);

        fill(255);
        textFont(titleFont);
        text(
                "HOSPITAL SIMULATION",
                width / 2,
                panelY + 90
        );

        drawButton(
                "NEW GAME",
                width / 2 - buttonWidth / 2,
                panelY + 165,
                buttonWidth,
                buttonHeight
        );

        drawButton(
                "MAPS",
                width / 2 - buttonWidth / 2,
                panelY + 245,
                buttonWidth,
                buttonHeight
        );

        drawButton(
                "EXIT",
                width / 2 - buttonWidth / 2,
                panelY + 325,
                buttonWidth,
                buttonHeight
        );

        fill(220);
        textFont(smallFont);
        textAlign(RIGHT, BOTTOM);
        text(
                "v1.0",
                panelX + panelWidth - 20,
                panelY + panelHeight - 18
        );
    }

    private void drawMaps() {
        drawBackground();

        fill(0, 0, 0, 170);
        rect(0, 0, width, height);

        int panelWidth = 700;
        int panelHeight = 500;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        fill(10, 15, 20, 200);
        stroke(240);
        strokeWeight(2);
        rect(panelX, panelY, panelWidth, panelHeight, 18);

        textAlign(CENTER, CENTER);

        fill(255);
        textFont(titleFont);
        text(
                "SELECT MAP",
                width / 2,
                panelY + 70
        );

        drawButton(
                "HOSPITAL 1",
                width / 2 - buttonWidth / 2,
                panelY + 140,
                buttonWidth,
                buttonHeight
        );

        drawButton(
                "BACK",
                width / 2 - buttonWidth / 2,
                panelY + 250,
                buttonWidth,
                buttonHeight
        );

        fill(210);
        textFont(smallFont);
        text(
                "Available maps",
                width / 2,
                panelY + 350
        );
    }

    private void drawPause() {
        // Keep the current map visible behind the pause menu.
        renderer.renderMap(map);

        fill(0, 0, 0, 150);
        noStroke();
        rect(0, 0, width, height);

        int panelWidth = 540;
        int panelHeight = 500;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        fill(10, 15, 20, 220);
        stroke(240);
        strokeWeight(2);
        rect(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                18
        );

        stroke(170);
        strokeWeight(1);
        rect(
                panelX + 10,
                panelY + 10,
                panelWidth - 20,
                panelHeight - 20,
                14
        );

        textAlign(CENTER, CENTER);

        fill(255);
        textFont(titleFont);
        text(
                "PAUSED",
                width / 2,
                panelY + 75
        );

        drawButton(
                "RESUME",
                width / 2 - buttonWidth / 2,
                panelY + 140,
                buttonWidth,
                buttonHeight
        );

        drawButton(
                "RESTART",
                width / 2 - buttonWidth / 2,
                panelY + 220,
                buttonWidth,
                buttonHeight
        );

        drawButton(
                "MAIN MENU",
                width / 2 - buttonWidth / 2,
                panelY + 300,
                buttonWidth,
                buttonHeight
        );

        fill(210);
        textFont(smallFont);
        text(
                "Press ESC to resume",
                width / 2,
                panelY + 410
        );
    }

    private void drawButton(
            String label,
            int x,
            int y,
            int buttonWidth,
            int buttonHeight) {

        boolean hovered = mouseX >= x
                && mouseX <= x + buttonWidth
                && mouseY >= y
                && mouseY <= y + buttonHeight;

        if (hovered) {
            fill(60, 75, 80, 230);
            stroke(255);
        } else {
            fill(25, 30, 35, 220);
            stroke(210);
        }

        strokeWeight(2);
        rect(
                x,
                y,
                buttonWidth,
                buttonHeight,
                8
        );

        fill(255);
        textFont(buttonFont);
        textAlign(CENTER, CENTER);

        text(
                label,
                x + buttonWidth/2,
                y + buttonHeight/2
        );
    }

    private void drawBackground() {
        if (menuBackground != null) {
            imageMode(CORNER);
            image(
                    menuBackground,
                    0,
                    0,
                    width,
                    height
            );
        } else {
            background(20);
        }
    }

    private void startGame() {
        if (mapLoader.load("maps/hospital1.txt")
                && mapLoader.isValid()) {
            map = mapLoader.getMap();
            currentScreen = Screen.SIMULATION;
        }
    }

    private void restartGame() {
        startGame();
    }

    @Override
    public void mousePressed() {

        if (currentScreen == Screen.MENU) {
            int panelHeight = 510;
            int panelY = (height - panelHeight) / 2;
            int buttonX = width / 2 - buttonWidth / 2;

            if (isInsideButton(
                    buttonX,
                    panelY + 165,
                    buttonWidth,
                    buttonHeight)) {
                startGame();

            } else if (isInsideButton(
                    buttonX,
                    panelY + 245,
                    buttonWidth,
                    buttonHeight)) {
                currentScreen = Screen.MAPS;

            } else if (isInsideButton(
                    buttonX,
                    panelY + 325,
                    buttonWidth,
                    buttonHeight)) {
                exit();
            }

        } else if (currentScreen == Screen.MAPS) {
            int panelHeight = 500;
            int panelY = (height - panelHeight) / 2;
            int buttonX = width / 2 - buttonWidth / 2;

            if (isInsideButton(
                    buttonX,
                    panelY + 140,
                    buttonWidth,
                    buttonHeight)) {
                startGame();

            } else if (isInsideButton(
                    buttonX,
                    panelY + 250,
                    buttonWidth,
                    buttonHeight)) {

                currentScreen = Screen.MENU;
            }

        } else if (currentScreen == Screen.PAUSE) {
            int panelHeight = 500;
            int panelY = (height - panelHeight) / 2;
            int buttonX = width / 2 - buttonWidth / 2;
            if (isInsideButton(
                    buttonX,
                    panelY + 140,
                    buttonWidth,
                    buttonHeight)) {

                currentScreen = Screen.SIMULATION;
            } else if (isInsideButton(
                    buttonX,
                    panelY + 220,
                    buttonWidth,
                    buttonHeight)) {
                restartGame();
            } else if (isInsideButton(
                    buttonX,
                    panelY + 300,
                    buttonWidth,
                    buttonHeight)) {
                currentScreen = Screen.MENU;
            }
        }
    }
    @Override
    public void keyPressed() {
        if (key == ESC) {
            key = 0;
            if (currentScreen == Screen.SIMULATION) {
                currentScreen = Screen.PAUSE;
            } else if (currentScreen == Screen.PAUSE) {
                currentScreen = Screen.SIMULATION;
            }
        }
    }
    private boolean isInsideButton(
            int x,
            int y,
            int buttonWidth,
            int buttonHeight) {
        return mouseX >= x
                && mouseX <= x + buttonWidth
                && mouseY >= y
                && mouseY <= y + buttonHeight;
    }
}