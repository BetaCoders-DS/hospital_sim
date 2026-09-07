package com.github.betacoders.render;

import processing.core.PApplet;

public class ProcessingRenderer implements RenderI {
    private final PApplet app;

    public ProcessingRenderer(PApplet app) {
        this.app = app;
    }

    @Override
    public void render() {
        app.background(255);
    }
}