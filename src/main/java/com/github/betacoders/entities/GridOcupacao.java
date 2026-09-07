package com.github.betacoders.entities;

public final class GridOcupacao {

  private final Pacient[][] grid; // matriz: cada celula guarda o paciente que esta ali, ou null se vazia

  public GridOcupacao(int largura, int altura) {
    grid = new Pacient[altura][largura]; // cria a matriz do mesmo tamanho do mapa
  }

  // Marca a celula como ocupada por um paciente
  public void ocupar(Position pos, Pacient paciente) {
    grid[pos.y][pos.x] = paciente;
  }

  // Libera a celula (paciente saiu dali)
  public void liberar(Position pos) {
    grid[pos.y][pos.x] = null;
  }

  // Consulta se a celula esta livre
  public boolean estaLivre(Position pos) {
    return grid[pos.y][pos.x] == null;
  }

  // Retorna quem esta na celula (ou null se estiver vazia)
  public Pacient pacienteEm(Position pos) {
    return grid[pos.y][pos.x];
  }

  // Move um paciente de uma posicao para outra em uma unica operacao
  public void mover(Position origem, Position destino, Pacient paciente) {
    liberar(origem);
    ocupar(destino, paciente);
  }

  // Limpa todo o grid, voltando todas as células a null (usado no reset geral)
  public void limpar() {
    for (Pacient[] linha : grid) {
      java.util.Arrays.fill(linha, null);
    }
  }
}