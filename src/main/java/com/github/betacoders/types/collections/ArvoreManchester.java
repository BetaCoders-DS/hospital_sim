package com.github.betacoders.types.collections;

import com.github.betacoders.types.NoManchester;
import com.github.betacoders.types.NoManchester.Cor;
import com.github.betacoders.types.NoManchester.Operador;

public class ArvoreManchester
{
  private final NoManchester[] nos = new NoManchester[31];

  public ArvoreManchester()
  {
    // As posicoes nao utilizadas permanecem nulas.
    nos[0] = new NoManchester(3, Operador.IGUAL, 1);
    nos[1] = new NoManchester(Cor.VERMELHA);
    nos[2] = new NoManchester(0, Operador.MENOR, 92);
    nos[5] = new NoManchester(Cor.LARANJA);
    nos[6] = new NoManchester(2, Operador.MAIOR_OU_IGUAL, 8);
    nos[13] = new NoManchester(Cor.AMARELA);
    nos[14] = new NoManchester(1, Operador.MAIOR_OU_IGUAL, 38);
    nos[29] = new NoManchester(Cor.VERDE);
    nos[30] = new NoManchester(Cor.AZUL);
  }

  public Cor classificar(float[] atributos)
  {
    if (atributos == null || atributos.length != 4)
    {
      throw new IllegalArgumentException("Informe os quatro atributos do paciente.");
    }

    // Valida tambem os atributos que podem nao ser visitados no percurso.
    for (int i = 0; i < atributos.length; i++)
    {
      if (!Float.isFinite(atributos[i]))
      {
        throw new IllegalArgumentException("Os atributos precisam ser numeros finitos.");
      }
    }

    int indice = 0;
    while (!nos[indice].folha())
    {
      if (nos[indice].testar(atributos))
      {
        indice = 2 * indice + 1;
      }
      else
      {
        indice = 2 * indice + 2;
      }
    }

    return nos[indice].cor();
  }
}
