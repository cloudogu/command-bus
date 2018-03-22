/**
 * MIT License
 *
 * Copyright (c) 2017 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.cb.decorator;

import java.util.concurrent.TimeUnit;

/**
 * Timer measures elapsed time in nanoseconds. The timer captures the time in nanoseconds during the creation. To print
 * the elapsed time use the toString method.
 */
class Timer {

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
