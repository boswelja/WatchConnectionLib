# serializers

This module provides some basic serialization support for all supported hosts.

## Data Serialization

### Creating a serializer

To create your own serializer, simply extend `MessageSerializer`

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

    override suspend fun deserialize(bytes: ByteArray?): MyData {
        // Deserialize ByteArray to MyData
    }
}
```

The list of message paths specified will be used to filter incoming messages for the serializer to handle.

### Sending and receiving typed data

Typed messages are managed by a [MessageHandler](https://github.com/boswelja/WatchConnectionLib/blob/main/serialization/src/commonMain/kotlin/com/boswelja/watchconnection/serialization/MessageHandler.kt).
`MessageHandler` accepts a base `MessageClient`, as well as a single `MessageSerializer`.

```kotlin
val messageHandler = MessageHandler<MyData>(
    MySerializer,
    messageClient
)
```

#### Sending messages

Call `messageHandler.send` and provide the target device UID, along with a `Message` with a data type matching your serializer data type.

```kotlin
messageHandler.send(
    device.uid,
    Message(
        "path-1",
        MyData()
    )
)
```

#### Receiving messages

Messages are received from a `Flow`. Collecting from the Flow automatically filters and deserializes received messages.

```kotlin
messageHandler.incomingMessages().collect { message ->
    // message.data is MyData
}
```
