package com.github.betacoders.simulation;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.entities.Patient.State;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.collections.LinkedList;

public class MedicQueue
{
  private final LinkedList<Patient> redPatients = new LinkedList<>();
  private final LinkedList<Patient> orangePatients = new LinkedList<>();
  private final LinkedList<Patient> yellowPatients = new LinkedList<>();
  private final LinkedList<Patient> greenPatients = new LinkedList<>();
  private final LinkedList<Patient> bluePatients = new LinkedList<>();

  private static final Color[] PRIORITY_ORDER = {
      Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE
  };

  public void enqueue(Patient patient)
  {
    if (patient == null)
    {
      throw new IllegalArgumentException("The patient cannot be null.");
    }

    if (patient.getState() != State.WAITING_FOR_MEDIC || patient.getManchesterColor() == null)
    {
      throw new IllegalStateException("The patient must be classified and waiting for a medic.");
    }

    LinkedList<Patient> queue = queueFor(patient.getManchesterColor());
    if (queue.contains(patient))
    {
      throw new IllegalStateException("The patient is already in the medic queue.");
    }

    queue.addLast(patient);
  }

  // A cor define a prioridade. Dentro de cada cor, vale a ordem de chegada.
  // O controlador define o medico e o destino depois de retirar o paciente.
  public Patient dequeueNext()
  {
    for (int i = 0; i < PRIORITY_ORDER.length; i++)
    {
      LinkedList<Patient> queue = queueFor(PRIORITY_ORDER[i]);
      if (!queue.isEmpty())
      {
        return queue.removeFirst();
      }
    }

    return null;
  }

  // Tamanho de cada fila por cor, na ordem de prioridade (vermelho para azul).
  public int[] sizes()
  {
    int[] out = new int[PRIORITY_ORDER.length];
    for (int i = 0; i < PRIORITY_ORDER.length; i++)
    {
      out[i] = queueFor(PRIORITY_ORDER[i]).size();
    }
    return out;
  }

  // Zera todas as filas (chamado durante o reset geral da simulacao).
  public void clear()
  {
    redPatients.clear();
    orangePatients.clear();
    yellowPatients.clear();
    greenPatients.clear();
    bluePatients.clear();
  }

  private LinkedList<Patient> queueFor(Color color)
  {
    switch (color)
    {
      case RED:
        return redPatients;
      case ORANGE:
        return orangePatients;
      case YELLOW:
        return yellowPatients;
      case GREEN:
        return greenPatients;
      case BLUE:
        return bluePatients;
      default:
        throw new IllegalArgumentException("Unknown Manchester color.");
    }
  }
}
