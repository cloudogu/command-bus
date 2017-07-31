# command-bus
CDI enabled Java Command-Bus

## Concepts

* `Command` - Marker Interface
* `CommandHandler` - One Implementation per `Command`. Provides `handle(CommandImplementation)` Method.
* `CommandBus` - Finds and calls the `CommandHandler` for each `Command`.
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

* Having the command-bus dependency on the classpath triggers the CDI extension that finds all `CommandHandler`s and registeres them with the appropriate `Command` in the `Registry`.
* Provide a Producer for the `CommandBus` that brings together `Registry` and `CDICommandBus`.
  This producer is the central place where decorators can be instantiated.
  See `CommandBusFactory` in tests, for example.
* Implement your `Command`s and `CommandHandler`s. See `CDIITCase`.
  
## Command Bus Decorators

First example is the logging decorator (`LoggingCommandBus`) that logs entering and leaving (including time of execution) of `CommandHandler`s.

## Future Tasks

* Return typed value from `CommandHandler.handle()`?
