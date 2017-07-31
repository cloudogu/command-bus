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

import de.triology.cb.Command;
import de.triology.cb.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command bus decorator which logs every execution of a command with its elapsed time.
 */
public class LoggingCommandBus implements CommandBus {

  static final Logger LOG = LoggerFactory.getLogger(LoggingCommandBus.class);

  private CommandBus decorated;

  /**
   * Creates a new command bus and delegates the execution to the given command.
   *
   * @param decorated command bus to decorate
   */
  public LoggingCommandBus(CommandBus decorated) {
    this.decorated = decorated;
  }

  @Override
  public <C extends Command> void execute(C command) {
    LOG.info("start command {}", command.getClass().getSimpleName());

    Timer timer = new Timer();
    decorated.execute(command);

    LOG.info("finished command {} in {}", command.getClass().getSimpleName(), timer);
  }
}
