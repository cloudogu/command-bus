# command-bus
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
   <version>348c18dfc1</version>
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

## Return values

* `Command`s can specify return values. See [`HelloCommand`](src/test/java/de/triology/cb/HelloCommand.java) and  [`HelloCommandHandler`](src/test/java/de/triology/cb/HelloCommandHandler.java) for example.
* If you don't want a return value, use `Void`. See [`ByeCommand`](src/test/java/de/triology/cb/ByeCommand.java) and  [`ByeCommandHandler`](src/test/java/de/triology/cb/ByeCommandHandler.java) for example.
