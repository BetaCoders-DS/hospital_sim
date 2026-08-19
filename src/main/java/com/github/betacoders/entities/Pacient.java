package com.github.betacoders.entities;

import com.github.betacoders.types.Position;

/**
 * Pacient
 */
public class Pacient {
  private Position pos;
  private StaticEntities target;
  private boolean preferential;
  private int ticketNum = 0; // Starts at 0, representing an invalid state

  public Pacient(Position pos, boolean preferential) {
    this.pos = pos;
    this.preferential = preferential;
    this.target = StaticEntities.TOTEM;
  }

  public void giveTicketNum(int ticketNum) {
    if (this.ticketNum == 0)
      throw new TicketAlreadyGivenException();
    if (ticketNum <= 0)
      throw new InvalidTicketNumberException(ticketNum);

    this.ticketNum = ticketNum;
  }

  public String getTicketString() {
    if (ticketNum <= 0)
      throw new InvalidTicketNumberException(ticketNum);

    StringBuilder out = new StringBuilder(5);
    out.append(preferential ? 'P' : 'N');
    out.append("%04d".formatted(ticketNum));

    return out.toString();
  }

  public boolean getPreferential() {
    return preferential;
  }

  public StaticEntities getTarget() {
    return target;
  }

  public void setTarget(StaticEntities e) {
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
