package de.triology.cb.decorator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Timer}.
 */
public class TimerTest {

  @Test
  public void properToStringFormat() {
    assertThat(new Timer().toString()).matches("[0-9]+(\\.[0-9]+)?μs");
  }

}