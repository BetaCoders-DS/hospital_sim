package com.github.betacoders.time;

import java.util.Random; //Gerador de numeros aleatorios

public final class GeneratorTimer {
  private static final Random random = new Random(); // instacia para gerar os numeros aleatorios

  private GeneratorTimer() {
  }

  // sorteia o tempo ate o proximo paciente nascer, usando distribuicao
  // exponencial
  public static double nextSpawn(double spawnAvg) {
    double u = random.nextDouble(); // numero aleatorio entre 0 e 1
    return -spawnAvg * Math.log(1 - u); // formula para converter "u" em um tempo exponencial
  }

  // Sorteia a duracao de um atendimento, usando distribuicao gaussiana com valor
  // minimo
  public static double serviceTime(double avg, double deviation, double min) {
    double base = avg + (deviation * random.nextGaussian()); // gera um valor em torno da media
    return Math.max(base, min); // garante que nunca fique abaixo do minimo
  }
}
