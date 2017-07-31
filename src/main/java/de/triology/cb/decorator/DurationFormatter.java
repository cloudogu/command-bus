package de.triology.cb.decorator;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * DurationFormatter formats duration with its abbreviate time unit.
 */
public final class DurationFormatter {

  private DurationFormatter(){}

  /**
   * Returns the formatted duration with the abbreviate time unit.
   *
   * @param duration duration
   * @param sourceUnit unit of duration
   *
   * @return formatted duration
   */
  public static String format(long duration, TimeUnit sourceUnit) {
    long durationInNanoSeconds = convertToNanoSeconds(duration, sourceUnit);
    return formatNanoSeconds(durationInNanoSeconds);
  }

  private static long convertToNanoSeconds(long duration, TimeUnit sourceUnit) {
    return TimeUnit.NANOSECONDS.convert(duration, sourceUnit);
  }

  private static String formatNanoSeconds(long durationInNanoSeconds) {
    TimeUnit targetUnit = chooseUnit(durationInNanoSeconds);
    double value = (double) durationInNanoSeconds / NANOSECONDS.convert(1, targetUnit);

    NumberFormat formatter = NumberFormat.getInstance();
    return formatter.format(value) + abbreviate(targetUnit);
  }

  private static TimeUnit chooseUnit(long nanos) {
    if (DAYS.convert(nanos, NANOSECONDS) > 0) {
      return DAYS;
    }
    if (HOURS.convert(nanos, NANOSECONDS) > 0) {
      return HOURS;
    }
    if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
      return MINUTES;
    }
    if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
      return SECONDS;
    }
    if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
      return MILLISECONDS;
    }
    if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
      return MICROSECONDS;
    }
    return NANOSECONDS;
  }

  private static String abbreviate(TimeUnit unit) {
    switch (unit) {
      case NANOSECONDS:
        return "ns";
      case MICROSECONDS:
        return "\u03bcs"; // μs
      case MILLISECONDS:
        return "ms";
      case SECONDS:
        return "s";
      case MINUTES:
        return "min";
      case HOURS:
        return "h";
      case DAYS:
        return "d";
      default:
        throw new AssertionError();
    }
  }

}
