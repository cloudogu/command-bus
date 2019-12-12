/**
 * MIT License
 *
 * Copyright (c) 2017 Cloudogu GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.cb.cdi;

import com.cloudogu.cb.ByeCommand;
import com.cloudogu.cb.CommandBus;
import com.cloudogu.cb.HelloCommand;
import com.cloudogu.cb.MessageCollector;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

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
    String actualStringReturnValue = commandBus.execute(new HelloCommand("hans"));
    Void actualVoidReturnValue = commandBus.execute(new ByeCommand("hans"));

    Assertions.assertThat(messageCollector.getMessages()).contains("hello hans", "bye hans");

    assertThat(actualStringReturnValue).isEqualTo("hello hans");
    assertThat(actualVoidReturnValue).isNull();
  }
}
