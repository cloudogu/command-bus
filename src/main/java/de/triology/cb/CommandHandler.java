package de.triology.cb;

/**
 * A handler for a {@link Command}.
 *
 * @param <C> type of command
 */
public interface CommandHandler<C extends Command> {

  /**
   * Handles the command.
   *
   * @param command command to handle
   */
  void handle(C command);

}
