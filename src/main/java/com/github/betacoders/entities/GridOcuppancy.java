package com.github.betacoders.entities;

import com.github.betacoders.types.Position;

public final class GridOcuppancy {
  private final Pacient[][] grid; // matriz: cada celula guarda o paciente que esta ali, ou null se vazia

  public GridOcuppancy(int width, int height) {
    grid = new Pacient[height][width]; // cria a matriz do mesmo tamanho do mapa
  }

  // Marca a celula como ocupada por um paciente
  public void occupy(Position pos, Pacient pacient) {
    grid[pos.y][pos.x] = pacient;
  }

  // Libera a celula (paciente saiu dali)
  public void free(Position pos) {
    grid[pos.y][pos.x] = null;
  }

  // Consulta se a celula esta livre
  public boolean isFree(Position pos) {
    return grid[pos.y][pos.x] == null;
  }

  // Retorna quem esta na celula (ou null se estiver vazia)
  public Pacient pacientOn(Position pos) {
    return grid[pos.y][pos.x];
  }

  // Move um paciente de uma posicao para outra em uma unica operacao
  public void move(Position from, Position to, Pacient pacient) {
    free(from);
    occupy(to, pacient);
  }

  // Limpa todo o grid, voltando todas as células a null (usado no reset geral)
  public void clean() {
    for (Pacient[] row : grid) {
      java.util.Arrays.fill(row, null);
    }
  }
}
