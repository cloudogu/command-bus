package de.triology.cb;

/**
 * The Command-Bus is able to execute commands, by passing the command object to its appropriate handler.
 */
public interface CommandBus {

  /**
   * Searches the handler and passes the command to it.
   *
   * @param command command object
   * @param <C> type of command
   */
  <C extends Command> void execute(C command);

}
