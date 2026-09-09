package com.github.betacoders.simulation;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.entities.Pacient.State;
import com.github.betacoders.types.collections.LinkedList;

public class FilaTriagem
{
  private final LinkedList<Pacient> normais = new LinkedList<>();
  private final LinkedList<Pacient> preferenciais = new LinkedList<>();
  private int preferenciaisConsecutivos = 0;

  public void enfileirar(Pacient paciente)
  {
    if (paciente == null)
    {
      throw new IllegalArgumentException("O paciente nao pode ser nulo.");
    }

    if (paciente.getState() != State.FILA_TRIAGEM)
    {
      throw new IllegalStateException("O paciente precisa estar aguardando triagem.");
    }

    LinkedList<Pacient> fila = paciente.preferential() ? preferenciais : normais;
    if (fila.contains(paciente))
    {
      throw new IllegalStateException("O paciente ja esta na fila de triagem.");
    }

    fila.addLast(paciente);
  }

  // Retira o proximo paciente. O controlador define a enfermeira e o destino.
  public Pacient retirarProximo()
  {
    if (!preferenciais.isEmpty() && (preferenciaisConsecutivos < 2 || normais.isEmpty()))
    {
      // Mantem o limite mesmo quando apenas a fila preferencial tem pacientes.
      if (preferenciaisConsecutivos < 2)
      {
        preferenciaisConsecutivos++;
      }

      return preferenciais.removeFirst();
    }

    if (!normais.isEmpty())
    {
      preferenciaisConsecutivos = 0;
      return normais.removeFirst();
    }

    return null;
  }
}
