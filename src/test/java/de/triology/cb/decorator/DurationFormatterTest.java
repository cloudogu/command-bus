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