package de.triology.cb.decorator;

import de.triology.cb.CommandBus;
import de.triology.cb.HelloCommand;
import io.prometheus.client.Counter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrometheusMetricsCommandBusTest {

  @Mock
  private CommandBus decorated;

  @Mock
  private Counter counter;

  @Mock
  private Counter.Child child;

  private PrometheusMetricsCommandBus commandBus;

  @Before
  public void setUp() throws Exception {
    when(counter.labels(HelloCommand.class.getName())).thenReturn(child);
    this.commandBus = new PrometheusMetricsCommandBus(decorated, counter);
  }

  @Test
  public void execute() throws Exception {
    HelloCommand hello = new HelloCommand("joe");
    commandBus.execute(hello);
    verify(decorated).execute(hello);
    verify(counter).labels(hello.getClass().getSimpleName());
    verify(child).inc();
  }

}