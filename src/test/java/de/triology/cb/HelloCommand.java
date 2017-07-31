package de.triology.cb;

public class HelloCommand implements Command {

  private final String name;

  public HelloCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
