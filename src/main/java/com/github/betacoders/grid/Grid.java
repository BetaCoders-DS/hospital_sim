package com.github.betacoders.grid;

/*
 * Grid
 * Classe genérica de armazenamento de objetos em grid.
 */
public class Grid<T> {
  public interface CellConsumer {
    void apply(int x, int y);
  }

  private int sizeX;
  private int sizeY;

  protected Object[] cells;

  public int sizeX() {
    return sizeX;
  }

  public int sizeY() {
    return sizeY;
  }

  public Grid(int w, int h) {
    this.sizeX = w;
    this.sizeY = h;

    this.cells = new Object[h * w];
  }

  public T get(int x, int y) {
    return (T) cells[y * sizeX + x];
  }

  public void set(int x, int y, T c) {
    cells[y * sizeX + x] = c;
  }

  public void forEach(CellConsumer f) {
    for (int y = 0; y < sizeY; ++y)
      for (int x = 0; x < sizeX; ++x) {
        f.apply(x, y);
      }
  }
}
