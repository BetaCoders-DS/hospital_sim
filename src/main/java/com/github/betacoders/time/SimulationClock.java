package com.github.betacoders.time;

public final class SimulationClock {
  private double timePassed = 0; // relogio logico da simulacaoo
  private long lastUpdateMillis; // ultimo instante real em que o relogio foi atualizado
  private boolean paused = false; // se true, o relogio para de avancar

  // Incia o relogio do zero (chamado ao comecar uma nova simulacao)
  public void start() {
    timePassed = 0; // zera o tempo do relogio
    lastUpdateMillis = System.currentTimeMillis(); // ancora a referencia no tempo real atual
    paused = false; // o relogio avanca
  }

  // Avanca o relogio logico da simulacao no tempo (chamado uma vez por frame do
  // loop principal)
  public void update() {
    if (paused) {
      return; // pausado, nao avanca o tempo
    }
    long now = System.currentTimeMillis(); // tempo real atual
    long deltaMillis = now - lastUpdateMillis; // quanto passou desde a ultima chamada
    timePassed += deltaMillis / 1000.0; // soma no relogio logico e converte para segundos
    lastUpdateMillis = now; // atualiza a referencia
  }

  // Pausa o relogio (chamado ao entrar no Menu de Pausa)
  public void pause() {
    paused = true; // o relogio pausa
  }

  // Reseta o relogio ao estado inicial (chamado ao clicar Resetar)
  public void reset() {
    timePassed = 0; // zera o tempo do relogio
    lastUpdateMillis = System.currentTimeMillis(); // ancora a referencia no tempo real de agora
    paused = false; // o relogio avanca
  }

  public double getTimePassed() {
    return timePassed; // retorna o tempo se passou desde que a simulacao comecou
  }

  public boolean isPaused() {
    return paused; // retorna se esta pausado ou nao
  }

}
