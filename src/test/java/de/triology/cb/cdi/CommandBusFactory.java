package de.triology.cb.cdi;

import de.triology.cb.CommandBus;
import de.triology.cb.decorator.LoggingCommandBus;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class CommandBusFactory {

  private Registry registry;

  @Inject
  public CommandBusFactory(Registry registry) {
    this.registry = registry;
  }

  @Any
  @Default
  @Produces
  public CommandBus create() {
    return new LoggingCommandBus(
      new CDICommandBus(registry)
    );
  }
}
