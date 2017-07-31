package de.triology.cb.cdi;

import de.triology.cb.Command;
import de.triology.cb.CommandHandler;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;

/**
 * CommandProvider creates a handler with enabled cdi injection.
 *
 * @param <H> type of handler
 */
@SuppressWarnings("unchecked")
public class CommandProvider<H extends CommandHandler<?>> implements Provider<H> {

  private final BeanManager beanManager;
  private final Class<? extends CommandHandler> handlerClass;

  CommandProvider(BeanManager beanManager, Class<? extends CommandHandler<?>> handlerClass) {
    this.beanManager = beanManager;
    this.handlerClass = handlerClass;
  }

  @Override
  public H get() {
    Bean<H> handlerBean = (Bean<H>) beanManager.getBeans(handlerClass).iterator().next();
    CreationalContext<H> context = beanManager.createCreationalContext(handlerBean);
    return (H) beanManager.getReference(handlerBean, handlerClass, context);
  }
}
