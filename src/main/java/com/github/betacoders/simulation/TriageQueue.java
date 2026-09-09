package com.github.betacoders.simulation;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.entities.Patient.State;
import com.github.betacoders.types.collections.LinkedList;

public class TriageQueue
{
  private final LinkedList<Patient> normalPatients = new LinkedList<>();
  private final LinkedList<Patient> preferentialPatients = new LinkedList<>();
  private int consecutivePreferentialPatients = 0;

  public void enqueue(Patient patient)
  {
    if (patient == null)
    {
      throw new IllegalArgumentException("The patient cannot be null.");
    }

    if (patient.getState() != State.WAITING_FOR_TRIAGE)
    {
      throw new IllegalStateException("The patient must be waiting for triage.");
    }

    LinkedList<Patient> queue = patient.preferential() ? preferentialPatients : normalPatients;
    if (queue.contains(patient))
    {
      throw new IllegalStateException("The patient is already in the triage queue.");
    }

    queue.addLast(patient);
  }

  // Zera as filas e o contador de preferenciais consecutivos (reset geral).
  public void clear()
  {
    normalPatients.clear();
    preferentialPatients.clear();
    consecutivePreferentialPatients = 0;
  }

  // Quantidade de pacientes na fila normal.
  public int normalSize()
  {
    return normalPatients.size();
  }

  // Quantidade de pacientes na fila preferencial.
  public int preferentialSize()
  {
    return preferentialPatients.size();
  }

  // Retira o proximo paciente. O controlador define a enfermeira e o destino.
  public Patient dequeueNext()
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
