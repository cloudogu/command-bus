package de.triology.cb.decorator;

import de.triology.cb.Command;
import de.triology.cb.CommandBus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;


public class ValidatingCommandBus implements CommandBus {

  private final CommandBus decorated;
  private final Validator validator;

  public ValidatingCommandBus(CommandBus decorated, Validator validator) {
    this.decorated = decorated;
    this.validator = validator;
  }

  @Override
  public <R, C extends Command<R>> R execute(C command) {
    Set<ConstraintViolation<C>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    return decorated.execute(command);
  }
}
