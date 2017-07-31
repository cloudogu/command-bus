package de.triology.cb;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageCollector {

  private List<String> messages = new ArrayList<>();

  void add(String message) {
    messages.add(message);
  }

  public List<String> getMessages() {
    return messages;
  }
}
