package com.github.betacoders.entities;

import com.github.betacoders.types.NoManchester.Cor;
import com.github.betacoders.types.collections.ArvoreManchester;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;

/**
 * Pacient
 * Única entidade móvel, é armazenada num grid secundario, e também
 * numa lista encadeada.
 */
public class Pacient
{
  public enum State
  {
    INDO_TOTEM,
    NO_TOTEM,
    FILA_TRIAGEM,
    INDO_TRIAGEM,
    EM_TRIAGEM,
    FILA_MEDICO,
    INDO_MEDICO,
    EM_CONSULTA,
    INDO_REMOVEDOR,
    REMOVIDO
  }

  private State state = State.INDO_TOTEM;
  private Position pos;
  private StaticEntities target;

  private Vitals vitals;
  private Cor corManchester;
  private boolean preferential;
  private int ticketNum = 0; // Starts at 0, representing an invalid state

  public Pacient(Position pos, boolean preferential) {
    this.pos = pos;
    this.preferential = preferential;
    this.target = new StaticEntities.Totem();
  }

  public Pacient(Position pos, boolean preferential, Vitals vitals)
  {
    this(pos, preferential);

    if (vitals == null)
    {
      throw new IllegalArgumentException("Os sinais vitais nao podem ser nulos.");
    }

    this.vitals = vitals;
  }

  public Cor getCorManchester()
  {
    return corManchester;
  }

  // O controlador chama este metodo quando o tempo de triagem termina.
  public void concluirTriagem(ArvoreManchester arvore)
  {
    if (state != State.EM_TRIAGEM)
    {
      throw new IllegalStateException("O paciente precisa estar em triagem.");
    }

    if (vitals == null)
    {
      throw new IllegalStateException("O paciente precisa ter sinais vitais cadastrados.");
    }

    if (arvore == null)
    {
      throw new IllegalArgumentException("A arvore de Manchester nao pode ser nula.");
    }

    float[] atributos = {
        vitals.oxigenSat(), vitals.bodyTemp(), vitals.painLevel(), vitals.conscious()
    };
    corManchester = arvore.classificar(atributos);
    mudarEstado(State.FILA_MEDICO);
  }

  public State getState()
  {
    return state;
  }

  // O controlador avisa as chegadas, chamadas e conclusoes de atendimento.
  // Esperar ou caminhar mais um quadro nao exige mudar de estado.
  public void mudarEstado(State novoEstado)
  {
    boolean permitida = false;

    switch (state)
    {
      case INDO_TOTEM:
        permitida = novoEstado == State.NO_TOTEM;
        break;
      case NO_TOTEM:
        permitida = novoEstado == State.FILA_TRIAGEM && ticketNum > 0;
        break;
      case FILA_TRIAGEM:
        permitida = novoEstado == State.INDO_TRIAGEM;
        break;
      case INDO_TRIAGEM:
        permitida = novoEstado == State.EM_TRIAGEM;
        break;
      case EM_TRIAGEM:
        permitida = novoEstado == State.FILA_MEDICO && corManchester != null;
        break;
      case FILA_MEDICO:
        permitida = novoEstado == State.INDO_MEDICO;
        break;
      case INDO_MEDICO:
        permitida = novoEstado == State.EM_CONSULTA;
        break;
      case EM_CONSULTA:
        permitida = novoEstado == State.INDO_REMOVEDOR;
        break;
      case INDO_REMOVEDOR:
        permitida = novoEstado == State.REMOVIDO;
        break;
      case REMOVIDO:
        break;
    }

    if (!permitida)
    {
      throw new IllegalStateException(
          "Transicao de estado invalida: " + state + " -> " + novoEstado);
    }

    state = novoEstado;
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

    if (state != State.NO_TOTEM)
    {
      throw new IllegalStateException("O paciente precisa estar no totem para receber a senha.");
    }

    this.ticketNum = ticketNum;
    mudarEstado(State.FILA_TRIAGEM);
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
