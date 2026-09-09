package com.github.betacoders;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.betacoders.entities.Patient;
import com.github.betacoders.types.Position;

/**
 * Unit test for simple App.
 */
public class AppTest {

  @Nested
  class PatientTests {
    Patient p;

    @BeforeEach
    void setup() {
      p = new Patient(new Position(10, 10), true);
    }

    @Test
    void throwsIfInvalid() {
      assertThrows(
          Patient.InvalidTicketNumberException.class,
          () -> p.giveTicketNum(-1));
    }
  }
}
