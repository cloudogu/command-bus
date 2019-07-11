# command-bus
[![Build Status](https://oss.cloudogu.com/jenkins/buildStatus/icon?job=cloudogu-github/command-bus/master)](https://oss.cloudogu.com/jenkins/blue/organizations/jenkins/cloudogu-github%2Fcommand-bus/branches/)
[![Quality Gates](https://sonarcloud.io/api/project_badges/measure?project=com.cloudogu.cb%3Acommand-bus-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.cloudogu.cb%3Acommand-bus-parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.cloudogu.cb%3Acommand-bus-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=com.cloudogu.cb%3Acommand-bus-parent)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.cloudogu.cb%3Acommand-bus-parent&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.cloudogu.cb%3Acommand-bus-parent)

CDI/Spring enabled Java Command-Bus

# Table of contents
<!-- Update with `doctoc --notitle README.md`. See https://github.com/thlorenz/doctoc -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


  - [Concepts](#concepts)
  - [Usage](#usage)
    - [Dependency for CDI](#dependency-for-cdi)
    - [Dependency for Spring](#dependency-for-spring)
    - [API](#api)
    - [Internals](#internals)
  - [Command Bus Decorators](#command-bus-decorators)
    - [Prometheus metric decorators](#prometheus-metric-decorators)
      - [PrometheusMetricsCountingCommandBus](#prometheusmetricscountingcommandbus)
      - [PrometheusMetricsTimingCommandBus](#prometheusmetricstimingcommandbus)
    - [Micrometer metric decorators](#micrometer-metric-decorators)
      - [MicrometerCountingCommandBus](#micrometercountingcommandbus)
      - [MicrometerTimingCommandBus](#micrometertimingcommandbus)
  - [Return values](#return-values)
- [Examples](#examples)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Concepts

* [`Command`](command-bus-core/src/main/java/de/triology/cb/Command.java) - Marker Interface
* [`CommandHandler`](command-bus-core/src/main/java/de/triology/cb/CommandHandler.java) - One Implementation per `Command`. Provides `handle(CommandImplementation)` Method.
* [`CommandBus`](command-bus-core/src/main/java/de/triology/cb/CommandBus.java) - Finds and calls the `CommandHandler` for each `Command`.
* `CommandBus` can be decorated, in order to implement cross-cutting concerns, such as logging, transaction handling, validation, autorization, metrics etc.

## Usage

Add the [latest stable version of command-bus](http://search.maven.org/#search|gav|1|g%3A%22com.cloudogu.cb%22%20AND%20a%3A%22command-bus-cdi%22) to the dependency management tool of your choice.
You can also get snapshot versions from our [snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/com/cloudogu/cb/) (for the most recent commit on develop branch).
To do so, add the following repo to your `pom.xml` or `settings.xml`:
```xml
<repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases><enabled>false</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
</repository>
```

There are different versions of command-bus for either CDI or spring.

### Dependency for CDI

```XML
<dependency>
    <groupId>com.cloudogu.cb</groupId>
    <artifactId>command-bus-cdi</artifactId>
    <version>1.0.1</version>
</dependency>
```

[![Maven Central](https://img.shields.io/maven-central/v/com.cloudogu.cb/command-bus-cdi.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.cloudogu.cb%22%20AND%20a%3A%22command-bus-cdi%22)


### Dependency for Spring

```XML
<dependency>
    <groupId>com.cloudogu.cb</groupId>
    <artifactId>command-bus-spring</artifactId>
    <version>1.0.1</version>
</dependency>
```

[![Maven Central](https://img.shields.io/maven-central/v/com.cloudogu.cb/command-bus-spring.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.cloudogu.cb%22%20AND%20a%3A%22command-bus-spring%22)

### API

* Bootstrapping
  * CDI: Having the `command-bus-cdi` dependency on the classpath triggers the CDI extension
  * Spring: All CommandHandlers must be within the application context (e.g. `@Component` in spring boot)
* Implement your [`Command`](command-bus-core/src/main/java/de/triology/cb/Command.java)s and the logic in appropriate 
  [`CommandHandler`](command-bus-core/src/main/java/de/triology/cb/CommandHandler.java)s. 
* You can now just inject the [`CommandBus`](command-bus-core/src/main/java/de/triology/cb/CommandBus.java) and pass your
 `Commands` to its `execute()` method. It will automatically pass it to the appropriate handler.
* Examples:
  * [`CDIITCase`](command-bus-cdi/src/test/java/de/triology/cb/cdi/CDIITCase.java)
  * [`SpringITCase`](command-bus-spring/src/test/java/de/triology/cb/spring/SpringITCase.java)
* If you want to decorate your command bus (for logging, metrics, etc.), a factory/producer for the `CommandBus` is the
  central place where decorators can be instantiated.
  It brings together your `CommandBus` (e.g. [`CDICommandBus`](command-bus-cdi/src/main/java/de/triology/cb/cdi/CDICommandBus.java),
  [`SpringCommandBus`](command-bus-spring/src/main/java/de/triology/cb/spring/SpringCommandBus.java)) with decorators 
  (see [bellow](#command-bus-decorators)).
  Example `CommandBusFactory`s:
  * [CDI](command-bus-cdi/src/test/java/de/triology/cb/cdi/CommandBusFactory.java)
  * [Spring](command-bus-spring/src/test/java/de/triology/cb/spring/CommandBusFactory.java)
   
### Internals
  
The `CommandHandler`s for CDI and Spring both use a `Registry` ([CDI](command-bus-cdi/src/main/java/de/triology/cb/cdi/Registry.java) / 
[Spring](command-bus-spring/src/main/java/de/triology/cb/spring/Registry.java)) to store `Command`s and 
`CommandHandler`s. Difference:
* CDI: The [`CDIExtension`](command-bus-cdi/src/main/java/de/triology/cb/cdi/CDIExtension.java) finds all `Command`s 
    and `CommandHandler`s and puts them on the `Registry`.
* Spring: The `Registry` itself gets all `Command`s and `CommandHandler`s from the application context.

## Command Bus Decorators

First example is the logging decorator ([`LoggingCommandBus`](command-bus-core/src/main/java/de/triology/cb/decorator/LoggingCommandBus.java)) that logs entering and leaving (including time of execution) of `CommandHandler`s.

### Prometheus metric decorators
The Command Bus provides two Prometheus metrics decorators. More information on Prometheus can be found on the
project's [website](https://prometheus.io).
In order to use them, make sure to provide the `io.prometheus:simpleclient` dependency on the classpath.

#### PrometheusMetricsCountingCommandBus
The `PrometheusMetricsCountingCommandBus` counts every executed command, using a Prometheus Counter. 
The counter to be used must be provided as a constructor parameter. For each type of command (i.e. it's class name) a 
label is created automatically.

#### PrometheusMetricsTimingCommandBus
The `PrometheusMetricsTimingCommandBus` captures the time a command's execution takes and provides the metric as a 
Prometheus Histogram. Similarly to the `PrometheusMetricsCountingCommandBus`, the Histogram needs to be provided as a 
constructor parameter.

### Micrometer metric decorators
The Command Bus provides two Micrometer metrics decorators. More information on Micrometer can be found on the
project's [website](https://micrometer.io).
In order to use them, make sure to provide a micrometer registry implementation such as prometheus `io.micrometer:micrometer-registry-prometheus`.

#### MicrometerCountingCommandBus

The `MicrometerCountingCommandBus` counts every executed command, using a Micrometer Counter e.g.:

```java
CommandBus commandBusImpl = ...;
MicrometerCountingCommandBus commandBus = new MicrometerCountingCommandBus(commandBusImpl, 
  commandClass -> Counter.builder("command.counter")
    .description("command execution counter")
    .tags("command", commandClass.getSimpleName())
    .register(Metrics.globalRegistry)
);
```

#### MicrometerTimingCommandBus

The `MicrometerTimingCommandBus` measures the elapsed time for every command execution by using a Micrometer a Micrometer Counter e.g.:

```java
CommandBus commandBusImpl = ...;
MicrometerTimingCommandBus commandBus = new MicrometerTimingCommandBus(commandBusImpl, 
  commandClass -> Timer.builder("command.timer")
    .description("command execution timer")
    .tags("command", commandClass.getSimpleName())
    .register(Metrics.globalRegistry)
);
```

## Return values

* `Command`s can specify return values. See [`HelloCommand`](command-bus-core/src/test/java/de/triology/cb/HelloCommand.java) and  [`de.triology.cb.EchoCommandHandler`](command-bus-core/src/test/java/de/triology/cb/HelloCommandHandler.java) for example.
* If you don't want a return value, use `Void`. See [`ByeCommand`](command-bus-core/src/test/java/de/triology/cb/ByeCommand.java) and  [`ByeCommandHandler`](command-bus-core/src/test/java/de/triology/cb/ByeCommandHandler.java) for example.

# Examples

[cloudogu/smeagol](https://github.com/cloudogu/smeagol)
