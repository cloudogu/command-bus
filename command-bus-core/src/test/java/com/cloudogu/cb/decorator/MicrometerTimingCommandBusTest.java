/**
 * MIT License
 *
 * Copyright (c) 2017 Cloudogu GmbH
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
package com.cloudogu.cb.decorator;

import com.cloudogu.cb.Command;
import com.cloudogu.cb.CommandBus;
import com.cloudogu.cb.EchoCommand;
import io.micrometer.core.instrument.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MicrometerTimingCommandBusTest {

  @Mock
  private CommandBus commandBus;

  @Mock
  private Timer timerOne;

  @Mock
  private Timer timerTwo;

  @Mock
  private Clock clock;

  private MicrometerTimingCommandBus decoratedCommandBus;

  private final Instant start = Instant.now();
  private Instant current = start;

  @Before
  public void setUp() {
    decoratedCommandBus = new MicrometerTimingCommandBus(commandBus, command -> {
      if (EchoCommand.class.isAssignableFrom(command)) {
        return timerOne;
      }
      return timerTwo;
    }, clock);

    when(clock.instant()).then(ic -> {
      current = current.plusSeconds(1L);
      return current;
    });
  }

  @Test
  public void execute() {
    decoratedCommandBus.execute(new EchoCommand("hello"));
    verify(timerOne).record(Duration.of(1L, ChronoUnit.SECONDS));

    decoratedCommandBus.execute(new OtherCommand());
    verify(timerTwo).record(Duration.of(1L, ChronoUnit.SECONDS));
  }

  public static class OtherCommand implements Command<Void> {

  }

}
