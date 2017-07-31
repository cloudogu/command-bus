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
