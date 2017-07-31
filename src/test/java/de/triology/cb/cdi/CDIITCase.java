package de.triology.cb.cdi;

import de.triology.cb.ByeCommand;
import de.triology.cb.CommandBus;
import de.triology.cb.HelloCommand;
import de.triology.cb.MessageCollector;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.assertj.core.api.Assertions.*;

@RunWith(Arquillian.class)
public class CDIITCase {

  @Deployment
  public static JavaArchive createDeployment() {
    JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
      .addPackage(CommandBus.class.getPackage())
      .addPackage(CDIExtension.class.getPackage());

    for (File file : new File("src/main/resources/META-INF").listFiles() ) {
      archive.addAsManifestResource(file);
    }

    return archive;
  }

  @Inject
  private CommandBus commandBus;

  @Inject
  private MessageCollector messageCollector;

  @Test
  public void execute() {
    commandBus.execute(new HelloCommand("hans"));
    commandBus.execute(new ByeCommand("hans"));

    assertThat(messageCollector.getMessages()).contains("hello hans", "bye hans");
  }
}
