package com.github.betacoders.render;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;

public class ProcessingRenderer implements RenderI {

    private final PApplet app;

    public ProcessingRenderer(PApplet app) {
        this.app = app;
    }

    @Override
    public void renderMap(Grid<StaticEntities> map) {
        app.background(255);
    }
}