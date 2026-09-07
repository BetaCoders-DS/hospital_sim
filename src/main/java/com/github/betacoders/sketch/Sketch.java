package com.github.betacoders.sketch;

import com.github.betacoders.render.ProcessingRenderer;
import processing.core.PApplet;

public class Sketch extends PApplet {

    private ProcessingRenderer renderer;

    @Override
    public void settings() {
        size(1000, 700);
    }

    @Override
    public void setup() {
        renderer = new ProcessingRenderer(this);
    }

    @Override
    public void draw() {
        renderer.render();
    }
}