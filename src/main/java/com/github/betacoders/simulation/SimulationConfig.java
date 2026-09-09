package com.github.betacoders.simulation;

public record SimulationConfig(
    double spawnAvg,
    double preferentialP,
    double triageAvg,
    double triageDev,
    double triageMin,
    double consultAvg,
    double consultDev,
    double consultMin,
    double o2Mean,
    double o2Dev,
    int o2Min,
    int o2Max,
    double tempMean,
    double tempDev,
    double tempMin,
    double tempMax,
    double painMean,
    double painDev,
    double consciousP) {

  public static SimulationConfig defaults() {
    return new SimulationConfig(
        5.0, 0.25,
        6.0, 2.0, 2.0,
        12.0, 4.0, 4.0,
        94.0, 6.0, 70, 100,
        37.0, 1.0, 34.0, 42.0,
        5.0, 3.0,
        0.05);
  }
}