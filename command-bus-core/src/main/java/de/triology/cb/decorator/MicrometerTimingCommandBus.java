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
import io.micrometer.core.instrument.Timer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command bus decorator which measures the elapsed time for a command execution by using a Micrometer {@link Timer}.
 */
public class MicrometerTimingCommandBus implements CommandBus {

  /**
   * Factory for creating Micrometer timers for the given command class.
   */
  @FunctionalInterface
  public interface TimerFactory {
    /**
     * Create timer for given command class.
     *
     * @param command class of command
     * @return Micrometer timer
     */
    Timer create(Class<? extends Command> command);
  }

  private final CommandBus decorated;
  private final TimerFactory timerFactory;
  private final Clock clock;

  private final Map<Class<? extends Command>, Timer> timers = new ConcurrentHashMap<>();

  /**
   * Creates a new {@link MicrometerTimingCommandBus}.
   *
   * @param decorated command bus to decorate
   * @param timerFactory factory to create a Micrometer counter
   */
  public MicrometerTimingCommandBus(CommandBus decorated, TimerFactory timerFactory) {
    this(decorated, timerFactory, Clock.systemDefaultZone());
  }

  /**
   * Creates a new {@link MicrometerTimingCommandBus}. This constructor should only be used for testing.
   *
   * @param decorated command bus to decorate
   * @param timerFactory factory to create a Micrometer counter
   * @param clock clock for calculating elapsed time
   */
  MicrometerTimingCommandBus(CommandBus decorated, TimerFactory timerFactory, Clock clock) {
    this.decorated = decorated;
    this.timerFactory = timerFactory;
    this.clock = clock;
  }

  @Override
  public <R, C extends Command<R>> R execute(C command) {
    Timer timer = timers.computeIfAbsent(command.getClass(), timerFactory::create);

    Instant now = Instant.now(clock);
    R result = decorated.execute(command);
    timer.record(Duration.between(now, Instant.now(clock)));

    return result;
  }
}
