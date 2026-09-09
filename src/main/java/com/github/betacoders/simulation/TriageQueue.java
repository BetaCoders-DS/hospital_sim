package com.github.betacoders.simulation;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.entities.Pacient.State;
import com.github.betacoders.types.collections.LinkedList;

public class TriageQueue
{
  private final LinkedList<Pacient> normalPatients = new LinkedList<>();
  private final LinkedList<Pacient> preferentialPatients = new LinkedList<>();
  private int consecutivePreferentialPatients = 0;

  public void enqueue(Pacient patient)
  {
    if (patient == null)
    {
      throw new IllegalArgumentException("The patient cannot be null.");
    }

    if (patient.getState() != State.WAITING_FOR_TRIAGE)
    {
      throw new IllegalStateException("The patient must be waiting for triage.");
    }

    LinkedList<Pacient> queue = patient.preferential() ? preferentialPatients : normalPatients;
    if (queue.contains(patient))
    {
      throw new IllegalStateException("The patient is already in the triage queue.");
    }

    queue.addLast(patient);
  }

  // Retira o proximo paciente. O controlador define a enfermeira e o destino.
  public Pacient dequeueNext()
  {
    if (!preferentialPatients.isEmpty() && (consecutivePreferentialPatients < 2 || normalPatients.isEmpty()))
    {
      // Mantem o limite mesmo quando apenas a fila preferencial tem pacientes.
      if (consecutivePreferentialPatients < 2)
      {
        consecutivePreferentialPatients++;
      }

      return preferentialPatients.removeFirst();
    }

    if (!normalPatients.isEmpty())
    {
      consecutivePreferentialPatients = 0;
      return normalPatients.removeFirst();
    }

    return null;
  }
}
