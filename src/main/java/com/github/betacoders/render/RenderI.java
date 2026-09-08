package com.github.betacoders.render;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;

public interface RenderI {

    void renderMap(Grid<StaticEntities> map);
}