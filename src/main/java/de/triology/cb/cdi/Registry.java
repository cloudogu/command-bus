package de.triology.cb.cdi;

import de.triology.cb.Command;
import de.triology.cb.CommandHandler;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry holds the mapping between a command and its handler.
 */
@Singleton
public class Registry {

  private Map<Class<? extends Command>, CommandProvider> providerMap = new HashMap<>();

  void register(Class<? extends Command> commandClass, CommandProvider provider){
    providerMap.put(commandClass, provider);
  }

  @SuppressWarnings("unchecked")
  <C extends Command> CommandHandler<C> get(Class<C> commandClass) {
    return providerMap.get(commandClass).get();
  }

}
