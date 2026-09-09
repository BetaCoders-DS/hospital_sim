package com.github.betacoders.entities;

import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.collections.ManchesterTree;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;

/**
 * Pacient
 * Única entidade móvel, é armazenada num grid secundario, e também
 * numa lista encadeada.
 */
public class Patient
{
  public enum State
  {
    GOING_TO_TOTEM,
    AT_TOTEM,
    WAITING_FOR_TRIAGE,
    GOING_TO_TRIAGE,
    IN_TRIAGE,
    WAITING_FOR_MEDIC,
    GOING_TO_MEDIC,
    IN_CONSULTATION,
    GOING_TO_REMOVER,
    REMOVED
  }

  private State state = State.GOING_TO_TOTEM;
  private Position pos;
  private StaticEntities target;

  private Vitals vitals;
  private Color manchesterColor;
  private boolean preferential;
  private int ticketNum = 0; // Starts at 0, representing an invalid state

  public Patient(Position pos, boolean preferential) {
    this.pos = pos;
    this.preferential = preferential;
    this.target = new StaticEntities.Totem();
  }

  public Patient(Position pos, boolean preferential, Vitals vitals)
  {
    this(pos, preferential);

    if (vitals == null)
    {
      throw new IllegalArgumentException("Vital signs cannot be null.");
    }

    this.vitals = vitals;
  }

  public Color getManchesterColor()
  {
    return manchesterColor;
  }

  // O controlador chama este metodo quando o tempo de triagem termina.
  public void completeTriage(ManchesterTree tree)
  {
    if (state != State.IN_TRIAGE)
    {
      throw new IllegalStateException("The patient must be in triage.");
    }

    if (vitals == null)
    {
      throw new IllegalStateException("The patient must have registered vital signs.");
    }

    if (tree == null)
    {
      throw new IllegalArgumentException("The Manchester tree cannot be null.");
    }

    float[] attributes = {
        vitals.oxigenSat(), vitals.bodyTemp(), vitals.painLevel(), vitals.conscious()
    };
    manchesterColor = tree.classify(attributes);
    changeState(State.WAITING_FOR_MEDIC);
  }

  public State getState()
  {
    return state;
  }

  // O controlador avisa as chegadas, chamadas e conclusoes de atendimento.
  // Esperar ou caminhar mais um quadro nao exige mudar de estado.
  public void changeState(State newState)
  {
    boolean allowed = false;

    switch (state)
    {
      case GOING_TO_TOTEM:
        allowed = newState == State.AT_TOTEM;
        break;
      case AT_TOTEM:
        allowed = newState == State.WAITING_FOR_TRIAGE && ticketNum > 0;
        break;
      case WAITING_FOR_TRIAGE:
        allowed = newState == State.GOING_TO_TRIAGE;
        break;
      case GOING_TO_TRIAGE:
        allowed = newState == State.IN_TRIAGE;
        break;
      case IN_TRIAGE:
        allowed = newState == State.WAITING_FOR_MEDIC && manchesterColor != null;
        break;
      case WAITING_FOR_MEDIC:
        allowed = newState == State.GOING_TO_MEDIC;
        break;
      case GOING_TO_MEDIC:
        allowed = newState == State.IN_CONSULTATION;
        break;
      case IN_CONSULTATION:
        allowed = newState == State.GOING_TO_REMOVER;
        break;
      case GOING_TO_REMOVER:
        allowed = newState == State.REMOVED;
        break;
      case REMOVED:
        break;
    }

    if (!allowed)
    {
      throw new IllegalStateException(
          "Invalid state transition: " + state + " -> " + newState);
    }

    state = newState;
  }

  public void giveTicketNum(int ticketNum)
  {
    if (this.ticketNum != 0)
    {
      throw new TicketAlreadyGivenException();
    }

    if (ticketNum <= 0)
    {
      throw new InvalidTicketNumberException(ticketNum);
    }

    if (state != State.AT_TOTEM)
    {
      throw new IllegalStateException("The patient must be at the totem to receive a ticket.");
    }

    this.ticketNum = ticketNum;
    changeState(State.WAITING_FOR_TRIAGE);
  }

  public String ticketString() {
    if (ticketNum <= 0)
      throw new InvalidTicketNumberException(ticketNum);

    StringBuilder out = new StringBuilder(5);
    out.append(preferential ? 'P' : 'N');
    out.append("%04d".formatted(ticketNum));

    return out.toString();
  }

  public boolean preferential() {
    return preferential;
  }

  public StaticEntities target() {
    return target;
  }

  public void target(StaticEntities e) {
    target = e;
  }

  public class TicketAlreadyGivenException extends RuntimeException {
    TicketAlreadyGivenException() {
      super("A ticket has already be given to this patient!");
    }
  }

  public class InvalidTicketNumberException extends RuntimeException {
    InvalidTicketNumberException(int num) {
      super("Can't give a ticket of number %d to a patient.".formatted(num));
    }
  }
}
