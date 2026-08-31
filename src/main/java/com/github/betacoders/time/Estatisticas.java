package com.github.betacoders.time;

public final class Estatisticas {

    private int totalAtendidos = 0;  //quantos pacientes ja sairam pelo Removedor
    private double somaTempoEspera = 0;  //soma dos tempos de espera de todos os atendidos
    private double somaTempoTotal = 0;  //soma dos tempos totais no sistema de todos os atendidos


    //Registra a saida de um paciente, acumulando seus tempos nas estatisticas
    public void registrarSaida(double tempoEsperaTotal, double tempoNoSistema) {
        totalAtendidos++;  //incrementa o contador de atendidos
        somaTempoEspera += tempoEsperaTotal;  //acumula tempo de espera
        somaTempoTotal += tempoNoSistema;  //acumula tempo total no sistema
    }

    //Calcula a media de tempo de espera
    public double mediaEspera() {
        if (totalAtendidos == 0) {
            return 0;  //retorna 0 se ninguem foi atendido ainda
        }
        return somaTempoEspera / totalAtendidos; //retorna a media do tempo de espera
    }

    //Calcula a media de tempo total no sistema
    public double mediaTempoTotal() {
        if (totalAtendidos == 0) { 
            return 0;  //retorna 0 se ninguem foi atendido
        }
        return somaTempoTotal / totalAtendidos;  //retorna a media do tempo total
    }

    //Retorna o total de atendidos
    public int getTotalAtendidos() {
    return totalAtendidos;
  }

}
