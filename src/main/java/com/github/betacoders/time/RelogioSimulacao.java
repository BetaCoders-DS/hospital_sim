package com.github.betacoders.time;

public final class RelogioSimulacao {
    private double tempoDecorrido = 0;   // relogio logico da simulacaoo
    private long ultimoMillis;           // ultimo instante real em que o relogio foi atualizado
    private boolean pausado = false;     // se true, o relogio para de avancar

    //Incia o relogio do zero
    public void iniciar() {
    tempoDecorrido = 0; //zera o tempo do relogio
    ultimoMillis = System.currentTimeMillis();  //ancora a referencia no tempo real atual
    pausado = false;  //se true, o relogio para de avancar
  }

}