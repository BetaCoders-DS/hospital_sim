package com.github.betacoders.types;

/**
 * Vitals
 */
public record Vitals(int oxigenSat, int bodyTemp, int painLevel, int conscious) {
  public Vitals {
    if (oxigenSat < 0 || oxigenSat > 100)
      throw new InvalidVitalsException();
    if (bodyTemp < 0 || bodyTemp > 50)
      throw new InvalidVitalsException();
    if (painLevel < 0 || painLevel > 10)
      throw new InvalidVitalsException();
    if (conscious != 0 && conscious != 1)
      throw new InvalidVitalsException();
  }

  public class InvalidVitalsException extends RuntimeException {
    InvalidVitalsException() {
      super("Ivalid vitals state");
    }
  }
}
