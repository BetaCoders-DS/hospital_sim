package com.github.betacoders.time;

public final class RelogioSimulacao {
    private double tempoDecorrido = 0;   // relogio logico da simulacaoo
    private long ultimoMillis;           // ultimo instante real em que o relogio foi atualizado
    private boolean pausado = false;     // se true, o relogio para de avancar

    //Incia o relogio do zero (chamado ao comecar uma nova simulacao)
    public void iniciar() {
    tempoDecorrido = 0; //zera o tempo do relogio
    ultimoMillis = System.currentTimeMillis();  //ancora a referencia no tempo real atual
    pausado = false;  //o relogio avanca
  }


  //Avanca o relogio logico da simulacao no tempo (chamado uma vez por frame do loop principal)
  public void atualizar() {
    if (pausado) {
      return; // pausado, nao avanca o tempo
    }
    long agora = System.currentTimeMillis();  // tempo real atual
    long deltaMillis = agora - ultimoMillis;  // quanto passou desde a ultima chamada
    tempoDecorrido += deltaMillis / 1000.0;  // soma no relogio logico e converte para segundos
    ultimoMillis = agora;  // atualiza a referencia
  }


  //Pausa o relogio (chamado ao entrar no Menu de Pausa)
  public void pausar() {
    pausado = true; // o relogio pausa
  }

  //Reseta o relogio ao estado inicial (chamado ao clicar Resetar)
  public void resetar() {
    tempoDecorrido = 0; //zera o tempo do relogio
    ultimoMillis = System.currentTimeMillis(); //ancora a referencia no tempo real de agora
    pausado = false; //o relogio avanca
  }

  public double getTempoDecorrido() {
    return tempoDecorrido; //retorna o tempo se passou desde que a simulacao comecou
  }


}