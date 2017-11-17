# command-bus
[![Build Status](https://opensource.triology.de/jenkins/buildStatus/icon?job=triologygmbh-github/command-bus/master)](https://opensource.triology.de/jenkins/blue/organizations/jenkins/triologygmbh-github%2Fcommand-bus/branches/)
[![Quality Gates](https://sonarcloud.io/api/badges/gate?key=de.triology.cb%3Acommand-bus)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=de.triology.cb%3Acommand-bus&metric=coverage)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)
[![Technical Debt](https://sonarcloud.io/api/badges/measure?key=de.triology.cb%3Acommand-bus&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)
[![JitPack](https://jitpack.io/v/triologygmbh/command-bus.svg)](https://jitpack.io/#triologygmbh/command-bus)

CDI enabled Java Command-Bus

## Concepts

* [`Command`](src/main/java/de/triology/cb/Command.java) - Marker Interface
* [`CommandHandler`](src/main/java/de/triology/cb/CommandHandler.java) - One Implementation per `Command`. Provides `handle(CommandImplementation)` Method.
* [`CommandBus`](src/main/java/de/triology/cb/CommandBus.java) - Finds and calls the `CommandHandler` for each `Command`.
* `CommandBus` can be decorated, in order to implement cross-cutting concerns, such as logging, transaction handling, validation, autorization, metrics etc.

## Usage (CDI)

You can use JitPack to configure command-bus as a dependency in your project.<br/>
For example when using maven, define the JitPack repository:

```XML
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
And the command-bus dependency:

```XML
<dependency>
    <groupId>com.github.triologygmbh</groupId>
    <artifactId>command-bus</artifactId>
   <version>0.1.0</version>
</dependency>
```
For further details and options refer to the [JitPack website](https://jitpack.io/#triologygmbh/command-bus).

* Having the command-bus dependency on the classpath triggers the CDI extension that finds all [`CommandHandler`](src/main/java/de/triology/cb/CommandHandler.java)s and registeres them with the appropriate [`Command`](src/main/java/de/triology/cb/Command.java) in the [`Registry`](src/main/java/de/triology/cb/cdi/Registry.java).
* Provide a Producer for the [`CommandBus`](src/main/java/de/triology/cb/CommandBus.java) that brings together [`Registry`](src/main/java/de/triology/cb/cdi/Registry.java) and [`CDICommandBus`](src/main/java/de/triology/cb/cdi/CDICommandBus.java).
  This producer is the central place where decorators can be instantiated.
  See [`CommandBusFactory`](src/test/java/de/triology/cb/cdi/CommandBusFactory.java) in tests, for example.
* Implement your [`Command`](src/main/java/de/triology/cb/Command.java)s and [`CommandHandler`](src/main/java/de/triology/cb/CommandHandler.java)s. See [`CDIITCase`](src/test/java/de/triology/cb/cdi/CDIITCase.java).
  
## Command Bus Decorators

First example is the logging decorator ([`LoggingCommandBus`](src/main/java/de/triology/cb/decorator/LoggingCommandBus.java)) that logs entering and leaving (including time of execution) of `CommandHandler`s.

### Prometheus metric decorators
The Triology Command Bus provides two Prometheus metrics decorators. More information on Prometheus can be found on the
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

## Return values

* `Command`s can specify return values. See [`HelloCommand`](src/test/java/de/triology/cb/HelloCommand.java) and  [`HelloCommandHandler`](src/test/java/de/triology/cb/HelloCommandHandler.java) for example.
* If you don't want a return value, use `Void`. See [`ByeCommand`](src/test/java/de/triology/cb/ByeCommand.java) and  [`ByeCommandHandler`](src/test/java/de/triology/cb/ByeCommandHandler.java) for example.
