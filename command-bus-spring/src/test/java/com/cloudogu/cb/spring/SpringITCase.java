/**
 * MIT License
 *
 * Copyright (c) 2017 Cloudogu GmbH
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
package com.cloudogu.cb.spring;

import com.cloudogu.cb.ByeCommand;
import com.cloudogu.cb.ByeCommandHandler;
import com.cloudogu.cb.CommandBus;
import com.cloudogu.cb.HelloCommand;
import com.cloudogu.cb.HelloCommandHandler;
import com.cloudogu.cb.MessageCollector;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {
  HelloCommandHandler.class,
  ByeCommandHandler.class,
  MessageCollector.class,
  Registry.class,
  CommandBusFactory.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringITCase {

  @Autowired
  private CommandBus commandBus;


  @Autowired
  private MessageCollector messageCollector;

  @Test
  public void execute() {
    String actualStringReturnValue = commandBus.execute(new HelloCommand("hans"));
    Void actualVoidReturnValue = commandBus.execute(new ByeCommand("hans"));

    Assertions.assertThat(messageCollector.getMessages()).contains("hello hans", "bye hans");

    assertThat(actualStringReturnValue).isEqualTo("hello hans");
    assertThat(actualVoidReturnValue).isNull();
  }

}