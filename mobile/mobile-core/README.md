# mobile-core

Provides all the base classes, interfaces and enums for implementing platforms, as well as classes to easily manage multiple platforms.
Note this module is useless without a platform to go with it.

## Supported Platforms

| Host | Supported |
| --- | :---: |
| Android | ✔️ |
| iOS | ✔️ |
| Wear OS | ❌ |
| watchOS | ❌ |

## Usage

### Messages

Messages are lightweight path/data pairs built to be your primary communication method between phone and watch.
Messages must have a target and path, however data is optional.

Messages are managed via a [MessageClient](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/message/MessageClient.kt).
You'll need to instantiate a MessageClient to be able to send and receive messages.

#### Data Serialization

`MessageClient` can automatically serialize and deserialize message data for you, as long as a [MessageSerializer](https://github.com/boswelja/WatchConnectionLib/blob/main/common/src/commonMain/kotlin/com/boswelja/watchconnection/common/message/MessageSerializer.kt) is provided.

Default serializers for common types are provided with the [serializers module](https://github.com/boswelja/WatchConnectionLib/tree/main/serializers).

Creating your own serializer is easy

```kotlin
object MySerializer : MessageSerializer<MyData>(
    messagePaths = setOf(
        "path-1",
        "path-2"
    )
) {
    override suspend fun serialize(data: MyData): ByteArray {
        // Serialize MyData to ByteArray
    }

    override suspend fun deserialize(bytes: ByteArray): MyData {
        // Deserialize ByteArray to MyData
    }
}
```

#### Create & Send a Message

See the [Message class](https://github.com/boswelja/WatchConnectionLib/blob/main/common/src/commonMain/kotlin/com/boswelja/watchconnection/common/message/Message.kt) for more info on creating a `Message`.

To send a message, just pass your target Watch and message to `MessageClient.sendMessage`

```kotlin
messageClient.sendMessage(
    to = targetWatch,
    message = myMessage
)
```

#### Receiving messages

You can collect incoming messages while your app is running via the `incomingMessages()` Flows in your `MessageClient`. There are [multiple incomingMessages variants](https://github.com/boswelja/WatchConnectionLib/blob/main/mobile/mobile-core/src/commonMain/kotlin/com/boswelja/watchconnection/core/message/MessageClient.kt) to choose from. Check out the link and pick the one that works best for you.

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
