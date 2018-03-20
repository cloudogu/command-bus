# command-bus
[![Build Status](https://opensource.triology.de/jenkins/buildStatus/icon?job=triologygmbh-github/command-bus/master)](https://opensource.triology.de/jenkins/blue/organizations/jenkins/triologygmbh-github%2Fcommand-bus/branches/)
[![Quality Gates](https://sonarcloud.io/api/badges/gate?key=de.triology.cb%3Acommand-bus)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=de.triology.cb%3Acommand-bus&metric=coverage)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)
[![Technical Debt](https://sonarcloud.io/api/badges/measure?key=de.triology.cb%3Acommand-bus&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=de.triology.cb%3Acommand-bus)

CDI enabled Java Command-Bus

## Concepts

* [`Command`](command-bus-core/src/main/java/de/triology/cb/Command.java) - Marker Interface
* [`CommandHandler`](command-bus-core/src/main/java/de/triology/cb/CommandHandler.java) - One Implementation per `Command`. Provides `handle(CommandImplementation)` Method.
* [`CommandBus`](command-bus-core/src/main/java/de/triology/cb/CommandBus.java) - Finds and calls the `CommandHandler` for each `Command`.
* `CommandBus` can be decorated, in order to implement cross-cutting concerns, such as logging, transaction handling, validation, autorization, metrics etc.

## Usage (CDI)

Add the [latest stable version of command-bus](http://search.maven.org/#search|gav|1|g%3A%22de.triology.cb%22%20AND%20a%3A%22command-bus%22) to the dependency management tool of your choice.

E.g. for maven

```XML
<dependency>
    <groupId>de.triology.cb</groupId>
    <artifactId>command-bus-cdi</artifactId>
    <version>1.0</version>
</dependency>
```
[![Maven Central](https://img.shields.io/maven-central/v/de.triology.cb/command-bus.svg)](http://search.maven.org/#search|gav|1|g%3A%22de.triology.cb%22%20AND%20a%3A%22command-bus%22)

You can get snapshot versions from maven central (for the most recent commit on develop branch) or via [JitPack](https://jitpack.io/#triologygmbh/command-bus) (note that JitPack uses different maven coordinates).  
[![JitPack](https://jitpack.io/v/triologygmbh/command-bus.svg)](https://jitpack.io/#triologygmbh/command-bus)

* Having the command-bus dependency on the classpath triggers the CDI extension that finds all [`CommandHandler`](command-bus-core/src/main/java/de/triology/cb/CommandHandler.java)s and registeres them with the appropriate [`Command`](command-bus-core/src/main/java/de/triology/cb/Command.java) in the [`Registry`](command-bus-cdi/src/main/java/de/triology/cb/cdi/Registry.java).
* Provide a Producer for the [`CommandBus`](command-bus-core/src/main/java/de/triology/cb/CommandBus.java) that brings together [`Registry`](command-bus-cdi/src/main/java/de/triology/cb/cdi/Registry.java) and [`CDICommandBus`](command-bus-cdi/src/main/java/de/triology/cb/cdi/CDICommandBus.java).
  This producer is the central place where decorators can be instantiated.
  See [`CommandBusFactory`](command-bus-cdi/src/test/java/de/triology/cb/cdi/CommandBusFactory.java) in tests, for example.
* Implement your [`Command`](command-bus-core/src/main/java/de/triology/cb/Command.java)s and [`CommandHandler`](command-bus-core/src/main/java/de/triology/cb/CommandHandler.java)s. See [`CDIITCase`](command-bus-cdi/src/test/java/de/triology/cb/cdi/CDIITCase.java).
  
## Command Bus Decorators

First example is the logging decorator ([`LoggingCommandBus`](command-bus-core/src/main/java/de/triology/cb/decorator/LoggingCommandBus.java)) that logs entering and leaving (including time of execution) of `CommandHandler`s.

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

* `Command`s can specify return values. See [`HelloCommand`](command-bus-core/src/test/java/de/triology/cb/HelloCommand.java) and  [`de.triology.cb.HelloCommandHandler`](command-bus-core/src/test/java/de/triology/cb/HelloCommandHandler.java) for example.
* If you don't want a return value, use `Void`. See [`ByeCommand`](command-bus-core/src/test/java/de/triology/cb/ByeCommand.java) and  [`ByeCommandHandler`](command-bus-core/src/test/java/de/triology/cb/ByeCommandHandler.java) for example.
