package de.triology.cb.cdi;

import de.triology.cb.Command;
import de.triology.cb.CommandBus;
import de.triology.cb.CommandHandler;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

/**
 * CDI backed Command-Bus.
 */
@Alternative
@SuppressWarnings("unchecked")
public class CDICommandBus implements CommandBus {

  private final Registry registry;

  /**
   * Creates a new instance with the given registry.
   *
   * @param registry registry
   */
  @Inject
  public CDICommandBus(Registry registry) {
    this.registry = registry;
  }

  @Override
  public <C extends Command> void execute(C command) {
    CommandHandler<C> commandHandler = (CommandHandler<C>) registry.get(command.getClass());
    commandHandler.handle(command);
  }
}
