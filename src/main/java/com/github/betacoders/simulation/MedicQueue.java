package com.github.betacoders.simulation;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.entities.Pacient.State;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.collections.LinkedList;

public class MedicQueue
{
  private final LinkedList<Pacient> redPatients = new LinkedList<>();
  private final LinkedList<Pacient> orangePatients = new LinkedList<>();
  private final LinkedList<Pacient> yellowPatients = new LinkedList<>();
  private final LinkedList<Pacient> greenPatients = new LinkedList<>();
  private final LinkedList<Pacient> bluePatients = new LinkedList<>();

  private static final Color[] PRIORITY_ORDER = {
      Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE
  };

  public void enqueue(Pacient patient)
  {
    if (patient == null)
    {
      throw new IllegalArgumentException("The patient cannot be null.");
    }

    if (patient.getState() != State.WAITING_FOR_MEDIC || patient.getManchesterColor() == null)
    {
      throw new IllegalStateException("The patient must be classified and waiting for a medic.");
    }

    LinkedList<Pacient> queue = queueFor(patient.getManchesterColor());
    if (queue.contains(patient))
    {
      throw new IllegalStateException("The patient is already in the medic queue.");
    }

    queue.addLast(patient);
  }

  // A cor define a prioridade. Dentro de cada cor, vale a ordem de chegada.
  // O controlador define o medico e o destino depois de retirar o paciente.
  public Pacient dequeueNext()
  {
    for (int i = 0; i < PRIORITY_ORDER.length; i++)
    {
      LinkedList<Pacient> queue = queueFor(PRIORITY_ORDER[i]);
      if (!queue.isEmpty())
      {
        return queue.removeFirst();
      }
    }

    return null;
  }

  private LinkedList<Pacient> queueFor(Color color)
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
