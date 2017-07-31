package de.triology.cb;

import javax.inject.Inject;

public class ByeCommandHandler implements CommandHandler<ByeCommand> {

  @Inject
  private MessageCollector messageCollector;

  @Override
  public void handle(ByeCommand command) {
    messageCollector.add("bye " + command.getName());
  }
}
