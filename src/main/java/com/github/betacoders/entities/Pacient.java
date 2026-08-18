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
    this.ticketNum = ticketNum;
  }

  public boolean getPreferential() {
    return preferential;
  }
}
