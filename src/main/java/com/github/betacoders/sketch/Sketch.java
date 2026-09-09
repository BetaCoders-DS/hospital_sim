package com.github.betacoders.sketch;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.maps.MapLoader;
import com.github.betacoders.render.ProcessingRenderer;
import com.github.betacoders.simulation.Simulation;
import com.github.betacoders.simulation.SimulationConfig;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.Position;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.io.File;
import java.io.FilenameFilter;

public class Sketch extends PApplet {

    private ProcessingRenderer renderer;
    private MapLoader mapLoader;
    private Grid<StaticEntities> map;
    private Simulation simulation;

    private PImage menuBackground;

    private PFont titleFont;
    private PFont buttonFont;
    private PFont smallFont;
    private PFont patientFont;

    private enum Screen {
        MENU,
        MAPS,
        SIMULATION,
        PAUSE,
        INVALID_MAP
    }

    private Screen currentScreen = Screen.MENU;

    private final int buttonWidth = 300;
    private final int buttonHeight = 60;

    /*
     * Controls how often the simulation advances.
     * A larger value makes patients move more slowly.
     */
    private static final long SIMULATION_STEP_INTERVAL = 150;
    private long lastSimulationStep = 0;

    private String[] availableMaps;
    private String selectedMap;

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
        patientFont = createFont("SansSerif.bold", 11);

        loadAvailableMaps();
    }

    @Override
    public void draw() {
        if (currentScreen == Screen.MENU) {
            drawMenu();

        } else if (currentScreen == Screen.MAPS) {
            drawMaps();

        } else if (currentScreen == Screen.SIMULATION) {
            drawSimulation();

        } else if (currentScreen == Screen.PAUSE) {
            drawPause();

        } else if (currentScreen == Screen.INVALID_MAP) {
            drawInvalidMap();
        }
    }

    private void drawSimulation() {
        if (simulation == null || map == null) {
            currentScreen = Screen.MENU;
            return;
        }

        /*
         * Processing renders many frames per second.
         * The simulation advances only every 150 ms.
         */
        long currentTime = millis();

        if (currentTime - lastSimulationStep >= SIMULATION_STEP_INTERVAL) {
            simulation.step();
            lastSimulationStep = currentTime;
        }

        renderer.renderMap(map);
        drawPatients();
        drawSimulationHud();
    }

    private void drawPatients() {
        float margin = 20;

        float availableWidth = width - margin * 2;
        float availableHeight = height - margin * 2;

        float cellWidth = availableWidth / map.sizeX();
        float cellHeight = availableHeight / map.sizeY();

        float cellSize = Math.min(cellWidth, cellHeight);

        float mapWidth = map.sizeX() * cellSize;
        float mapHeight = map.sizeY() * cellSize;

        float offsetX = (width - mapWidth) / 2;
        float offsetY = (height - mapHeight) / 2;

        for (Patient patient : simulation.patients()) {
            if (patient.getState() == Patient.State.REMOVED) {
                continue;
            }

            Position position = patient.pos();

            float x = offsetX + position.x * cellSize;
            float y = offsetY + position.y * cellSize;

            drawPatient(patient, x, y, cellSize);
        }
    }

    private void drawPatient(
            Patient patient,
            float x,
            float y,
            float size) {

        float centerX = x + size / 2;
        float centerY = y + size / 2;

        float radius = size * 0.30f;

        if (patient.preferential()) {
            fill(255, 215, 0);
        } else {
            fill(70, 130, 255);
        }

        stroke(20);
        strokeWeight(2);

        ellipse(
                centerX,
                centerY,
                radius * 2,
                radius * 2
        );

        drawManchesterIndicator(
                patient,
                centerX,
                centerY - radius * 0.9f,
                radius * 0.45f
        );

        fill(20);
        noStroke();

        textAlign(CENTER, CENTER);
        textFont(patientFont);

        text(
                patient.id(),
                centerX,
                centerY
        );
    }

    private void drawManchesterIndicator(
            Patient patient,
            float x,
            float y,
            float radius) {

        Color color = patient.getManchesterColor();

        if (color == null) {
            return;
        }

        if (color == Color.RED) {
            fill(220, 40, 40);

        } else if (color == Color.ORANGE) {
            fill(255, 140, 0);

        } else if (color == Color.YELLOW) {
            fill(255, 220, 40);

        } else if (color == Color.GREEN) {
            fill(50, 190, 80);

        } else {
            fill(80, 150, 220);
        }

        stroke(20);
        strokeWeight(1);

        ellipse(
                x,
                y,
                radius * 2,
                radius * 2
        );
    }

    private void drawSimulationHud() {
        int hudHeight = 42;

        fill(10, 15, 20, 190);
        noStroke();
        rect(0, 0, width, hudHeight);

        stroke(180, 150);
        strokeWeight(1);
        line(0, hudHeight, width, hudHeight);

        textAlign(LEFT, CENTER);

        fill(255);
        textFont(smallFont);

        text(
                "HOSPITAL SIMULATION",
                20,
                hudHeight / 2
        );

        fill(210);
        textFont(smallFont);

        text(
                getMapDisplayName(selectedMap),
                260,
                hudHeight / 2
        );

        if (simulation != null) {

            fill(220);

            text(
                    "PATIENTS: " + simulation.activeCount(),
                    500,
                    hudHeight / 2
            );

            text(
                    "SERVED: " + simulation.totalServed(),
                    670,
                    hudHeight / 2
            );

            int triageQueue =
                    simulation.normalQueueSize()
                            + simulation.preferentialQueueSize();

            text(
                    "TRIAGE QUEUE: " + triageQueue,
                    800,
                    hudHeight / 2
            );

            int[] medicQueues =
                    simulation.medicQueueSizes();

            int medicQueue =
                    medicQueues[0]
                            + medicQueues[1]
                            + medicQueues[2];

            text(
                    "MEDIC QUEUE: " + medicQueue,
                    1010,
                    hudHeight / 2
            );
        }

        textAlign(RIGHT, CENTER);

        fill(220);
        textFont(smallFont);

        text(
                "ESC - PAUSE",
                width - 20,
                hudHeight
        );
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

        rect(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                18
        );

        textAlign(CENTER, CENTER);

        fill(255);
        textFont(titleFont);

        text(
                "SELECT MAP",
                width / 2,
                panelY + 70
        );

        int mapButtonY = panelY + 140;

        if (availableMaps.length == 0) {

            fill(220);
            textFont(smallFont);

            text(
                    "No maps available",
                    width / 2,
                    mapButtonY + 30
            );

        } else {

            int maxMaps = 4;

            for (int i = 0;
                 i < availableMaps.length && i < maxMaps;
                 i++) {

                String mapName =
                        getMapDisplayName(availableMaps[i]);

                drawButton(
                        mapName,
                        width / 2 - buttonWidth / 2,
                        mapButtonY + i * 75,
                        buttonWidth,
                        buttonHeight
                );
            }
        }

        drawButton(
                "BACK",
                width / 2 - buttonWidth / 2,
                panelY + 420,
                buttonWidth,
                buttonHeight
        );

        fill(210);
        textFont(smallFont);

        text(
                availableMaps.length + " map(s) available",
                width / 2,
                panelY + 390
        );
    }

    private void drawPause() {
        if (map != null) {
            renderer.renderMap(map);
            drawPatients();
        }

        fill(0, 0, 0, 75);
        noStroke();
        rect(0, 0, width, height);

        int panelWidth = 540;
        int panelHeight = 500;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        fill(10, 15, 20, 190);
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

    private void drawInvalidMap() {
        drawBackground();

        fill(0, 0, 0, 175);
        noStroke();
        rect(0, 0, width, height);

        int panelWidth = 600;
        int panelHeight = 350;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        fill(10, 15, 20, 210);
        stroke(220);
        strokeWeight(2);

        rect(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                18
        );

        textAlign(CENTER, CENTER);

        fill(255);
        textFont(titleFont);

        text(
                "INVALID MAP",
                width / 2,
                panelY + 75
        );

        fill(220);
        textFont(smallFont);

        text(
                "The selected map is invalid.",
                width / 2,
                panelY + 145
        );

        text(
                "Check its dimensions, entities and paths.",
                width / 2,
                panelY + 175
        );

        drawButton(
                "BACK",
                width / 2 - buttonWidth / 2,
                panelY + 235,
                buttonWidth,
                buttonHeight
        );
    }

    private void drawButton(
            String label,
            int x,
            int y,
            int buttonWidth,
            int buttonHeight) {

        boolean hovered =
                mouseX >= x
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
                x + buttonWidth / 2,
                y + buttonHeight / 2
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

    private void loadAvailableMaps() {
        File mapsDirectory =
                new File("src/main/resources/maps");

        if (!mapsDirectory.exists()
                || !mapsDirectory.isDirectory()) {

            availableMaps = new String[0];
            return;
        }

        FilenameFilter mapFilter =
                new FilenameFilter() {

                    @Override
                    public boolean accept(
                            File directory,
                            String name) {

                        return name
                                .toLowerCase()
                                .endsWith(".txt");
                    }
                };

        String[] maps =
                mapsDirectory.list(mapFilter);

        if (maps == null) {
            availableMaps = new String[0];
        } else {
            availableMaps = maps;
        }
    }

    private String getMapDisplayName(String fileName) {
        if (fileName == null) {
            return "";
        }

        String name = fileName;

        if (name.toLowerCase().endsWith(".txt")) {
            name = name.substring(
                    0,
                    name.length() - 4
            );
        }

        name = name.replace("_", " ");
        name = name.replace("-", " ");

        return name.toUpperCase();
    }

    private void startGame() {
        if (selectedMap == null) {

            if (availableMaps.length == 0) {
                return;
            }

            selectedMap = availableMaps[0];
        }

        if (mapLoader.load(
                "maps/" + selectedMap)
                && mapLoader.isValid()) {

            map = mapLoader.getMap();

            SimulationConfig config =
                    SimulationConfig.defaults();

            simulation =
                    new Simulation(
                            map,
                            config
                    );

            lastSimulationStep = millis();

            currentScreen = Screen.SIMULATION;

        } else {
            simulation = null;
            currentScreen = Screen.INVALID_MAP;
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

            int buttonX =
                    width / 2 - buttonWidth / 2;

            if (isInsideButton(
                    buttonX,
                    panelY + 165,
                    buttonWidth,
                    buttonHeight)) {

                if (availableMaps.length > 0) {
                    selectedMap = availableMaps[0];
                    startGame();
                }

            } else if (isInsideButton(
                    buttonX,
                    panelY + 245,
                    buttonWidth,
                    buttonHeight)) {

                loadAvailableMaps();
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

            int buttonX =
                    width / 2 - buttonWidth / 2;

            int mapButtonY =
                    panelY + 140;

            int maxMaps = 4;

            for (int i = 0;
                 i < availableMaps.length && i < maxMaps;
                 i++) {

                int buttonY =
                        mapButtonY + i * 75;

                if (isInsideButton(
                        buttonX,
                        buttonY,
                        buttonWidth,
                        buttonHeight)) {

                    selectedMap = availableMaps[i];
                    startGame();
                    return;
                }
            }

            if (isInsideButton(
                    buttonX,
                    panelY + 420,
                    buttonWidth,
                    buttonHeight)) {

                currentScreen = Screen.MENU;
            }

        } else if (currentScreen == Screen.PAUSE) {

            int panelHeight = 500;
            int panelY =
                    (height - panelHeight) / 2;

            int buttonX =
                    width / 2 - buttonWidth / 2;

            if (isInsideButton(
                    buttonX,
                    panelY + 140,
                    buttonWidth,
                    buttonHeight)) {

                if (simulation != null) {
                    simulation.resume();
                }

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

                simulation = null;
                map = null;
                currentScreen = Screen.MENU;
            }

        } else if (currentScreen == Screen.INVALID_MAP) {

            int panelHeight = 350;
            int panelY = (height - panelHeight) / 2;

            int buttonX =
                    width / 2 - buttonWidth / 2;

            if (isInsideButton(
                    buttonX,
                    panelY + 235,
                    buttonWidth,
                    buttonHeight)) {

                currentScreen = Screen.MAPS;
            }
        }
    }

    @Override
    public void keyPressed() {

        if (key == ESC) {

            key = 0;

            if (currentScreen == Screen.SIMULATION) {

                if (simulation != null) {
                    simulation.pause();
                }

                currentScreen = Screen.PAUSE;

            } else if (currentScreen == Screen.PAUSE) {

                if (simulation != null) {
                    simulation.resume();
                }

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