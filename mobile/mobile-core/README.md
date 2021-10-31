# mobile-core

Provides all the base classes, interfaces and enums for implementing platforms, as well as classes to easily manage multiple platforms.
Note this module is useless without a platform to go with it.

## Supported Platforms

| Host | Supported |
| --- | :---: |
| Android | ✔️ |
| iOS | ✔️ |

## Usage

### Messages

Messages are lightweight path/data pairs built to be your primary communication method between phone and watch.
Messages must have a target and path, however data is optional.

Messages are managed via a [MessageHandler](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/message/MessageHandler.kt).
You'll need to instantiate a MessageClient to be able to send and receive messages.

#### Create & Send a Message

Sending a message is simple, just pass your target Watch and message to `MessageClient.sendMessage`

```kotlin
val result = messageClient.sendMessage(
    to = watch.uid,
    message = Message(
        path = "path",
        data = null,
        priority = Message.Priority.LOW
    )
)
```

#### Receiving messages

You can collect incoming messages while your app is running via the `incomingMessages()` Flows in your `MessageClient`.

```kotlin
messageClient.incomingMessages().collect { message ->
    // Do something on message received
}
```

### Discovery & Capabilities

[DiscoveryClient](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/discovery/DiscoveryClient.kt) allows you to find available watches and manage capabilities.

A capability is a string a device can announce to it's connected peers.
Capabilities are commonly used to announce a device supports some specific feature.
Devices can have any number of capabilities, and devices can be queried based on the capabilities they announce.

#### Getting Available Watches

Getting a list of available watches is as easy as collecting from the `allWatches()` Flow exposed by your `DiscoveryClient`.

```kotlin
discoveryClient.allWatches().collect { allWatches ->
    // Do something with allWatches
}
```

#### Managing Capabilities

`DiscoveryClient` provides functions to add and remove local capabilities. Take a look at [the source](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/discovery/DiscoveryClient.kt) to find the function you need.

#### Discovering Capabilities

`DiscoveryClient` provides functions to look up watches and their capabilities as needed. Take a look at [the source](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/discovery/DiscoveryClient.kt) to find a function that suits your needs.
