package com.github.betacoders.simulation;

/**
 * Simulation
 */
public class Simulation {


    public void resetSimulation() {
  clock.reset(); //zera o relogio logico da simulacao
  statistics.reset(); //zera as metricas do painel

  triageQueueNormal.clear(); //fila de triagem normal
  triageQueuePreferential.clear(); //fila de triagem preferencial

  redQueue.clear(); //fila de atendimento medico vermelha
  orangeQueue.clear(); //fila laranja
  yellowQueue.clear(); //fila amarela
  greenQueue.clear(); //fila verde
  blueQueue.clear(); //fila azul

  pacients.clear(); //lista de pacientes ativos no hospital

  for (Seat s : seats) {
    s.free(); //libera cada assento
  }

  nurse.free(); //libera a enfermeira

  for (Medic m : medics) {
    m.free(); //libera cada medico
  }

  occupationGrid.clear(); //limpa o grid secundario de ocupacao dos pacientes
}
}
