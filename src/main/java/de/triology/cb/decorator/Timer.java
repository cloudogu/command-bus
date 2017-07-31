package de.triology.cb.decorator;

import java.util.concurrent.TimeUnit;

/**
 * Timer measures elapsed time in nanoseconds. The timer captures the time in nanoseconds during the creation. To print
 * the elapsed time use the toString method.
 */
public class Timer {

  private long startedAt;

  /**
   * Creates a new timer and captures the start date.
   */
  public Timer() {
    this.startedAt = System.nanoTime();
  }

  /**
   * Returns the elapsed time in nanoseconds.
   *
   * @return elapsed time in nanoseconds
   */
  public long elapsed() {
    return System.nanoTime() - startedAt;
  }

  @Override
  public String toString() {
    return DurationFormatter.format(elapsed(), TimeUnit.NANOSECONDS);
  }

}
