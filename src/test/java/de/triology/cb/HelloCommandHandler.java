package de.triology.cb;

import javax.inject.Inject;

public class HelloCommandHandler implements CommandHandler<HelloCommand> {

  @Inject
  private MessageCollector messageCollector;

  @Override
  public void handle(HelloCommand command) {
    messageCollector.add("hello " + command.getName());
  }
}
