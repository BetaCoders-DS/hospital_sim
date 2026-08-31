package com.github.betacoders.time;

import java.util.Random; //Gerador de numeros aleatorios


public final class GeradorTempo {
    private static final Random random = new Random(); //instacia para gerar os numeros aleatorios

    private GeradorTempo(); {
    }

    //sorteia o tempo  ate o proximo paciente nascer, usando distribuicao exponencial
    public static double proximoSpawn(double mediaSpawn) {
        double u = random.nextDouble();  //numero aleatorio entre 0 e 1
        return -mediaSpawn * Math.log(1 - u); //formula para converter "u" em um tempo exponencial
    }


    //Sorteia a  duracao de um atendimento, usando distribuicao gaussiana com valor minimo 
    public static double tempoAtendimento(double media, double desvio, double minimo) {
    double base = media + (desvio * random.nextGaussian()); //gera um valor em torno da media
    return Math.max(base, minimo);    //garante que nunca fique abaixo do minimo
  }
}