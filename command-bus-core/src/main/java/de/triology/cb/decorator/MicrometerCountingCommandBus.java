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
import io.micrometer.core.instrument.Counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command bus decorator counting the executed commands using a Micrometer {@link Counter}
 */
public class MicrometerCountingCommandBus implements CommandBus {

  /**
   * Factory for creating Micrometer counters for the given command class.
   */
  @FunctionalInterface
  public interface CounterFactory {
    /**
     * Create counter for given command class.
     *
     * @param command class of command
     * @return Micrometer counter
     */
    Counter create(Class<? extends Command> command);
  }

  private final CommandBus decorated;
  private final CounterFactory counterFactory;
  private final Map<Class<? extends Command>,Counter> counters = new ConcurrentHashMap<>();

  /**
   * Creates a new {@link MicrometerCountingCommandBus}.
   *
   * @param decorated command bus to decorate
   * @param counterFactory factory to create a Micrometer counter
   */
  public MicrometerCountingCommandBus(CommandBus decorated, CounterFactory counterFactory) {
    this.decorated = decorated;
    this.counterFactory = counterFactory;
  }

  @Override
  public <R, C extends Command<R>> R execute(C command) {
    counters.computeIfAbsent(command.getClass(), counterFactory::create).increment();
    return decorated.execute(command);
  }
}
