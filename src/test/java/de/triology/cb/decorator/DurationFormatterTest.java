package de.triology.cb.decorator;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DurationFormatter}.
 */
public class DurationFormatterTest {

  @Test
  public void format() {
    assertThat(DurationFormatter.format(1000, TimeUnit.NANOSECONDS)).isEqualTo("1μs");
    assertThat(DurationFormatter.format(1000, TimeUnit.MICROSECONDS)).isEqualTo("1ms");
    assertThat(DurationFormatter.format(1000, TimeUnit.MILLISECONDS)).isEqualTo("1s");
    assertThat(DurationFormatter.format(60, TimeUnit.SECONDS)).isEqualTo("1min");
    assertThat(DurationFormatter.format(60, TimeUnit.MINUTES)).isEqualTo("1h");
    assertThat(DurationFormatter.format(24, TimeUnit.HOURS)).isEqualTo("1d");

    assertThat(DurationFormatter.format(12, TimeUnit.MINUTES)).isEqualTo("12min");
    assertThat(DurationFormatter.format(2, TimeUnit.SECONDS)).isEqualTo("2s");
    assertThat(DurationFormatter.format(120, TimeUnit.SECONDS)).isEqualTo("2min");
    assertThat(DurationFormatter.format(360, TimeUnit.MINUTES)).isEqualTo("6h");
  }

}