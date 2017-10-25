package de.triology.cb.decorator;

import de.triology.cb.Command;
import de.triology.cb.CommandBus;
import io.prometheus.client.Counter;

/**
 * Command bus decorator counting the executed commands using a Prometheus {@link Counter}
 */
public class PrometheusMetricsCommandBus implements CommandBus {

  private CommandBus decorated;
  private final Counter counter;

  /**
   * Creates a new PrometheusMetricsCommandBus
   *
   * @param decorated command bus to decorate
   * @param counter counter to increment on executing commands
   */
  public PrometheusMetricsCommandBus(CommandBus decorated, Counter counter) {
    this.decorated = decorated;
    this.counter = counter;
  }

  /**
   * Delegates the provided command to the decorated command bus and increases the given counter using the command's
   * classname as a label
   * @param command command object
   * @param <R> type of return value
   * @param <C> type of command
   */
  @Override
  public <R, C extends Command<R>> R execute(C command) {
    counter.labels(command.getClass().getSimpleName()).inc();
    return decorated.execute(command);
  }

}
