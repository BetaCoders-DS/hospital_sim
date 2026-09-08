package com.github.betacoders.time;

public final class Statistics {

  private int totalServed = 0; // quantos pacientes ja sairam pelo Removedor
  private double waitingTimeSum = 0; // soma dos tempos de espera de todos os atendidos
  private double systemTimeSum = 0; // soma dos tempos totais no sistema de todos os atendidos

  // Registra a saida de um paciente, acumulando seus tempos nas estatisticas
  public void logPacientExit(double waitingTime, double systemTime) {
    totalServed++; // incrementa o contador de atendidos
    waitingTimeSum += waitingTime; // acumula tempo de espera
    systemTimeSum += systemTime; // acumula tempo total no sistema
  }

  // Calcula a media de tempo de espera
  public double waitingTimeAvg() {
    if (totalServed == 0) {
      return 0; // retorna 0 se ninguem foi atendido ainda
    }
    return waitingTimeSum / totalServed; // retorna a media do tempo de espera
  }

  // Calcula a media de tempo total no sistema
  public double systemTimeAvg() {
    if (totalServed == 0) {
      return 0; // retorna 0 se ninguem foi atendido
    }
    return systemTimeSum / totalServed; // retorna a media do tempo total
  }

  // Retorna o total de atendidos
  public int getTotalServed() {
    return totalServed;
  }

  // Zera todas as metricas (chamado durante o reset geral da simulacao)
  public void reset() {
    totalServed = 0; // zera o total de atendidos
    waitingTimeSum = 0; // zera o tempo total de espera
    systemTimeSum = 0; // zera o tempo total
  }

}
