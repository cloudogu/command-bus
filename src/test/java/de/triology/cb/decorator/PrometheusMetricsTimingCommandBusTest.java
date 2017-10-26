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
package de.triology.cb.decorator;

import de.triology.cb.CommandBus;
import de.triology.cb.HelloCommand;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrometheusMetricsTimingCommandBusTest {
  @Mock
  private CommandBus decorated;

  @Mock
  private Histogram histogram;

  @Mock
  private Histogram.Child child;

  @Mock
  private Histogram.Timer timer;

  private PrometheusMetricsTimingCommandBus commandBus;

  @Before
  public void setUp() throws Exception {
    when(histogram.labels(HelloCommand.class.getSimpleName())).thenReturn(child);
    when(child.startTimer()).thenReturn(timer);
    commandBus = new PrometheusMetricsTimingCommandBus(decorated, histogram);
  }

  @Test
  public void execute() throws Exception {
    HelloCommand helloCommand = new HelloCommand("July");
    commandBus.execute(helloCommand);
    verify(histogram).labels(HelloCommand.class.getSimpleName());
    verify(decorated).execute(helloCommand);
    verify(timer).observeDuration();
  }

}