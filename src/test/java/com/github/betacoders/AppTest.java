package com.github.betacoders;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Pacient;
import com.github.betacoders.types.Position;

/**
 * Unit test for simple App.
 */
public class AppTest {

  @Nested
  class PacientTests {
    Pacient p;

    @BeforeEach
    void setup() {
      p = new Pacient(new Position(10, 10), true);
    }

    @Test
    void throwsIfInvalid() {
      assertThrows(
          Pacient.InvalidTicketNumberException.class,
          () -> p.giveTicketNum(-1));
    }
  }
}
