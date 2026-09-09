package com.github.betacoders;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;

public final class TestMaps {

  private TestMaps() {
  }

  // Hospital 20x12 com corredores abertos, sala de espera central e todos os
  // quatro pontos de atendimento obrigatorios. Layout generico o bastante para
  // a regra de caminhada do Wavefront nunca estagnar a simulacao inteira.
  public static Grid<StaticEntities> hospitalGrid() {
    int w = 20;
    int h = 12;
    Grid<StaticEntities> g = new Grid<>(w, h);
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        g.set(x, y, new StaticEntities.Floor());
      }
    }
    for (int y = 0; y < h; ++y) {
      g.set(0, y, new StaticEntities.Wall());
      g.set(w - 1, y, new StaticEntities.Wall());
    }
    for (int x = 0; x < w; ++x) {
      g.set(x, 0, new StaticEntities.Wall());
      g.set(x, h - 1, new StaticEntities.Wall());
    }

    g.set(1, 1, new StaticEntities.Generator());
    g.set(9, 1, new StaticEntities.Totem());
    g.set(8, 8, new StaticEntities.Nurse());
    g.set(12, 8, new StaticEntities.Medic());
    g.set(18, 10, new StaticEntities.Remover());

    // Sala de espera central: dois blocos 2x2 de assentos.
    g.set(5, 5, new StaticEntities.Seat());
    g.set(6, 5, new StaticEntities.Seat());
    g.set(5, 6, new StaticEntities.Seat());
    g.set(6, 6, new StaticEntities.Seat());
    g.set(13, 5, new StaticEntities.Seat());
    g.set(14, 5, new StaticEntities.Seat());
    g.set(13, 6, new StaticEntities.Seat());
    g.set(14, 6, new StaticEntities.Seat());

    // Pilares isolados: forcam desvios no Wavefront sem estrangular o fluxo.
    g.set(10, 4, new StaticEntities.Wall());
    g.set(11, 5, new StaticEntities.Wall());
    g.set(9, 9, new StaticEntities.Wall());

    return g;
  }

  public static Position nursePos() {
    return new Position(8, 8);
  }

  public static Position medicPos() {
    return new Position(12, 8);
  }

  public static Grid<StaticEntities> parse(String[] rows) {
    int h = rows.length;
    int w = rows[0].length();
    Grid<StaticEntities> g = new Grid<>(w, h);
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        g.set(x, y, switch (rows[y].charAt(x)) {
          case '#' -> new StaticEntities.Wall();
          case 'G' -> new StaticEntities.Generator();
          case 'R' -> new StaticEntities.Remover();
          case 'T' -> new StaticEntities.Totem();
          case 'A' -> new StaticEntities.Seat();
          case 'E' -> new StaticEntities.Nurse();
          case 'M' -> new StaticEntities.Medic();
          default -> new StaticEntities.Floor();
        });
      }
    }
    return g;
  }
}