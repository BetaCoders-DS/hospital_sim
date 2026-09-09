package com.github.betacoders.types;

public class NoManchester
{
  public enum Cor
  {
    VERMELHA, LARANJA, AMARELA, VERDE, AZUL
  }

  public enum Operador
  {
    IGUAL, MENOR, MAIOR_OU_IGUAL
  }

  private final int indiceAtributo;
  private final Operador operador;
  private final float limite;
  private final Cor cor;

  // Atributos: 0 = saturacao, 1 = temperatura, 2 = dor, 3 = consciencia alterada.
  public NoManchester(int indiceAtributo, Operador operador, float limite)
  {
    if (indiceAtributo < 0 || indiceAtributo > 3)
    {
      throw new IllegalArgumentException("Indice de atributo invalido.");
    }

    if (operador == null || !Float.isFinite(limite))
    {
      throw new IllegalArgumentException("Comparacao invalida.");
    }

    this.indiceAtributo = indiceAtributo;
    this.operador = operador;
    this.limite = limite;
    this.cor = null;
  }

  public NoManchester(Cor cor)
  {
    if (cor == null)
    {
      throw new IllegalArgumentException("A folha precisa de uma cor.");
    }

    this.indiceAtributo = -1;
    this.operador = null;
    this.limite = 0;
    this.cor = cor;
  }

  public boolean folha()
  {
    return cor != null;
  }

  public Cor cor()
  {
    return cor;
  }

  public boolean testar(float[] atributos)
  {
    if (folha())
    {
      throw new IllegalStateException("Uma folha nao possui teste.");
    }

    if (atributos == null || atributos.length != 4)
    {
      throw new IllegalArgumentException("Informe os quatro atributos do paciente.");
    }

    float valor = atributos[indiceAtributo];
    if (!Float.isFinite(valor))
    {
      throw new IllegalArgumentException("O atributo precisa ser um numero finito.");
    }

    switch (operador)
    {
      case IGUAL:
        return valor == limite;
      case MENOR:
        return valor < limite;
      case MAIOR_OU_IGUAL:
        return valor >= limite;
      default:
        throw new IllegalStateException("Operador desconhecido.");
    }
  }
}
