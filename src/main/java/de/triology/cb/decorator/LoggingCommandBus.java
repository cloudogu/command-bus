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
