package de.triology.cb.cdi;

import de.triology.cb.Command;
import de.triology.cb.CommandHandler;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * CDI Extension to find and register all available commands.
 */
@SuppressWarnings("unchecked")
public class CDIExtension implements Extension {

  private Map<Class<? extends Command>, Class<? extends CommandHandler<?>>> commandHandlers = new HashMap<>();

  /**
   * Captures all command handlers.
   *
   * @param target cdi event
   * @param <H> handler type
   */
  public <H extends CommandHandler<?>> void captureCommandHandlers(@Observes ProcessInjectionTarget<H> target) {
    Class<H> handler = target.getAnnotatedType().getJavaClass();
    for ( Type type : target.getAnnotatedType().getTypeClosure() ) {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type genericParameterType = parameterizedType.getActualTypeArguments()[0];
        if (genericParameterType instanceof Class) {
          register((Class<? extends Command>) genericParameterType, handler);
        }
      }
    }
  }

  /**
   * Registers all captured handlers on the {@link Registry}.
   * @param event cdi event
   * @param beanManager cdi bean manager
   */
  public void register(@Observes AfterBeanDiscovery event, final BeanManager beanManager) {
    Registry registry = getRegistry(beanManager);
    commandHandlers.forEach((commandClass, handlerClass) -> {
      registry.register(commandClass, new CommandProvider(beanManager, handlerClass));
    });
  }

  private void register(Class<? extends Command> command, Class<? extends CommandHandler<?>> handler) {
    commandHandlers.put(command, handler);
  }

  private Registry getRegistry(BeanManager beanManager) {
    Bean<Registry> registryBean = (Bean<Registry>) beanManager.getBeans(Registry.class).iterator().next();
    CreationalContext<Registry> context = beanManager.createCreationalContext(registryBean);
    return (Registry) beanManager.getReference(registryBean, Registry.class, context);
  }
}
