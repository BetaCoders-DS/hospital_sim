package com.github.betacoders.sketch;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.maps.MapLoader;
import com.github.betacoders.render.ProcessingRenderer;
import processing.core.PApplet;

public class Sketch extends PApplet {

    private ProcessingRenderer renderer;
    private MapLoader mapLoader;
    private Grid<StaticEntities> map;

    @Override
    public void settings() {
        size(1000, 700);
    }

    @Override
    public void setup() {
        renderer = new ProcessingRenderer(this);
        mapLoader = new MapLoader(this);

        if (mapLoader.load("maps/hospital1.txt")
                && mapLoader.isValid()) {
            map = mapLoader.getMap();
        }
    }

    @Override
    public void draw() {
        renderer.renderMap(map);
    }
}