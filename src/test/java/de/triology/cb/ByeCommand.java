package de.triology.cb;

public class ByeCommand implements Command {

  private final String name;

  public ByeCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
