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
  public void register(@Observes AfterDeploymentValidation event, final BeanManager beanManager) {
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
