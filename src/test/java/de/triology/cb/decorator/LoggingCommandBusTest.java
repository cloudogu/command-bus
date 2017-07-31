package de.triology.cb.decorator;

import com.thekua.spikes.LogbackCapturingAppender;
import de.triology.cb.CommandBus;
import de.triology.cb.HelloCommand;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link LoggingCommandBus}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoggingCommandBusTest {

  @Mock
  private CommandBus decorated;

  @InjectMocks
  private LoggingCommandBus loggingCommandBus;

  @After
  public void cleanUp() {
    LogbackCapturingAppender.cleanUpAll();
  }

  @Test
  public void execute() {
    LogbackCapturingAppender capturing = LogbackCapturingAppender.weaveInto(LoggingCommandBus.LOG);

    HelloCommand hello = new HelloCommand("joe");
    loggingCommandBus.execute(hello);
    verify(decorated).execute(hello);

    List<String> messages = capturing.getCapturedLogMessages();
    assertThat(messages.get(0)).contains("start").contains("HelloCommand");
    assertThat(messages.get(1)).contains("finish").contains("HelloCommand");
  }

}