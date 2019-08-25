package de.triology.cb.decorator;

import de.triology.cb.Command;
import de.triology.cb.CommandBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidatingCommandBusTest {

  @Mock
  private CommandBus commandBus;

  private ValidatingCommandBus decoratedCommandBus;

  @Before
  public void setUpCommandBus() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    decoratedCommandBus = new ValidatingCommandBus(commandBus,  factory.getValidator());
  }

  @Test(expected = ConstraintViolationException.class)
  public void shouldThrowValidationException() {
    decoratedCommandBus.execute(new SampleCommand(null));
  }

  @Test
  public void shouldCallDecorated() {
    SampleCommand command = new SampleCommand("valid");
    decoratedCommandBus.execute(command);
    verify(commandBus).execute(command);
  }

  public static class SampleCommand implements Command<Void> {

    @NotNull
    private String value;

    public SampleCommand(@NotNull String value) {
      this.value = value;
    }
  }
}
