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
package com.cloudogu.cb.decorator;

import com.cloudogu.cb.CommandBus;
import com.cloudogu.cb.EchoCommand;
import io.prometheus.client.Counter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrometheusMetricsCountingCommandBusTest {

  @Mock
  private CommandBus commandBus;

  @Mock
  private Counter counter;

  @Mock
  private Counter.Child child;

  private PrometheusMetricsCountingCommandBus decoratedCommandBus;

  @Before
  public void setUp() {
    when(counter.labels(EchoCommand.class.getSimpleName())).thenReturn(child);
    this.decoratedCommandBus = new PrometheusMetricsCountingCommandBus(commandBus, counter);
  }

  @Test
  public void execute() {
    EchoCommand hello = new EchoCommand("joe");
    decoratedCommandBus.execute(hello);
    verify(commandBus).execute(hello);
    verify(counter).labels(hello.getClass().getSimpleName());
    verify(child).inc();
  }

}
