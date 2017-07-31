package de.triology.cb.cdi;

import de.triology.cb.CommandHandler;
import de.triology.cb.HelloCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Tests {@link CDICommandBus}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CDICommandBusTest {

  @Mock
  private Registry registry;

  @Mock
  private CommandHandler<HelloCommand> handler;

  @InjectMocks
  private CDICommandBus commandBus;

  @Test
  public void execute() {
    when(registry.get(HelloCommand.class)).thenReturn(handler);

    HelloCommand command = new HelloCommand("joe");
    commandBus.execute(command);

    verify(handler).handle(command);
  }

}