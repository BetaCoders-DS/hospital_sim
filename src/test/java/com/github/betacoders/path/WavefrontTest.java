package com.github.betacoders.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;

public class WavefrontTest {

  private static Grid<StaticEntities> parse(String[] rows) {
    int h = rows.length;
    int w = rows[0].length();
    Grid<StaticEntities> g = new Grid<>(w, h);
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        g.set(x, y, switch (rows[y].charAt(x)) {
          case '#' -> new StaticEntities.Wall();
          case 'N' -> new StaticEntities.Nurse();
          case 'M' -> new StaticEntities.Medic();
          default -> new StaticEntities.Floor();
        });
      }
    }
    return g;
  }

  @Test
  void computesManhattanDistancesAcrossWall() {
    String[] rows = {
        "..#....",
        "..#....",
        "..#....",
    };
    Grid<Integer> dist = Wavefront.calculate(parse(rows), new Position(0, 0));
    assertEquals(0, dist.get(0, 0));
    assertEquals(1, dist.get(1, 0));
    assertEquals(2, dist.get(1, 1));
    assertEquals(-1, dist.get(2, 0));
    assertEquals(-1, dist.get(2, 2));
    assertEquals(-1, dist.get(4, 0));
    assertEquals(-1, dist.get(6, 2));
  }

  @Test
  void treatsNurseAndMedicCellsAsUnwalkable() {
    String[] rows = {
        "NM...",
        ".....",
    };
    Grid<Integer> dist = Wavefront.calculate(parse(rows), new Position(4, 1));
    assertEquals(-1, dist.get(0, 0));
    assertEquals(-1, dist.get(1, 0));
    assertEquals(4, dist.get(0, 1));
    assertEquals(0, dist.get(4, 1));
  }
}