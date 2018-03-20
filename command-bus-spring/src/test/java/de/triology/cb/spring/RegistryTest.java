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
package de.triology.cb.spring;

import de.triology.cb.CommandHandler;
import de.triology.cb.HelloCommand;
import de.triology.cb.HelloCommandHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistryTest {

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private HelloCommandHandler helloCommandHandler;

  @Test
  public void testRegistration() {
    String[] commandHandlers = new String[]{"helloCommandHandler"};
    when(applicationContext.getBeanNamesForType(CommandHandler.class)).thenReturn(commandHandlers);

    Class type = HelloCommandHandler.class;
    when(applicationContext.getType("helloCommandHandler")).thenReturn(type);

    when(applicationContext.getBean(HelloCommandHandler.class)).thenReturn(helloCommandHandler);

    Registry registry = new Registry(applicationContext);
    CommandHandler<String, HelloCommand> handler = registry.get(HelloCommand.class);

    assertThat(handler).isInstanceOf(HelloCommandHandler.class);
  }

}
