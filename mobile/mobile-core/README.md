# Core

Provides all the base classes, interfaces and enums for implementing platforms, as well as classes to easily manage multiple platforms.

## Usage

### Messages

Messages are sent and received from an instance of `MessageClient`.

All messages are sent and received as path/data maps, where a message must have a path and optionally contains data.

Type safety is available and recommended through the use of `MessageSerializer` implementations. See below for more info.

```kotlin
val messageClient = MessageClient(
    serializers = listOf(
        MyDataSerializer // Add any serializers you'd like to use here. This list can be empty
    ),
    platforms = listOf(
        wearOSMessagePlatform // Add any platforms you'd like to support here. This list cannot be empty.
    )
)
```

#### Data serialization

`MessageClient` can automatically serialize and deserialize message data for you, as long as a `MessageSerializer` is provided.

```kotlin
object MyDataSerializer : MessageSerializer<MyType>(
    messagePaths = setOf( // A set of message paths that will have serialized data for 'MyType'
        "message-path-1",
        "message-path-2"
    )
) {
    override suspend fun deserialize(bytes: ByteArray): MyType {
        // Deserialize bytes to 'MyType'
    }
    override suspend fun serialize(data: MyType): ByteArray {
        // Serialize data to 'ByteArray'
    }
}
```

We provide some serializers for `String`, `Int`, `Long` and `Boolean` by default. To use these, you only need to construct them with a set of message paths.

#### Sending messages

To send a typed message, call the appropriate function on your `MessageClient` instance.

```kotlin
coroutineScope.launch {
    messageClient.sendMessage(
        to = targetWatch,
        message = TypedMessage(
            path = "my-message",
            data = true // Or any other type you need
        ),
        priority = MessageProirity.LOW // Can be LOW or HIGH
    )
}
```

You can also send a raw `ByteArray`, or no data at all by instead constructing a `ByteArrayMessage`.

#### Receiving messages

##### Flow collection

You can collect incoming messages while your app is running via Kotlin Flows. To do this, call the appropriate function on your `MessageClient` instance.

```kotlin
coroutineScope.launch {
    messageClient.incomingMessages()
        .collect { message ->
            // Note the message type is ReceivedMessage<Any?>. This is due to automatic deserialization.
            // If you have no serializers set up, you could use rawIncomingMessages() instead
        }
}
```

Alternatively, if you only need a set of messages that have a specific data type you can pass a `MessageSerializer` to `incomingMessages()`.

```kotlin
coroutineScope.launch {
    messageClient.incomingMessages(MyDataSerializer)
        .collect { message ->
            // Note the message type is identical to your serializer data type. This is due to automatic deserialization.
        }
}
```

##### Manifest-declared receivers

You can also register to receive messages via a manifest-declared broadcast receiver.

To do this, create a class extending `TypedMessageReceiver` and register it in your app manifest with an intent filter for the action `com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED`.

Your resulting class should look like this:
```kotlin
class WatchMessageReceiver : TypedMessageReceiver(MyDataSerializer) {
    override suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<MyType>
    ) {
        // Handle message here
    }
}
```

Alternatively, you can collect the raw message data by extending `MessageReceiver` instead.

```kotlin
class WatchMessageReceiver : MesageReceiver() {
    override suspend fun onMessageReceived(
        context: Context,
        message: ReceivedMessage<ByteArray?>
    ) {
        // Handle message here
    }
}
```
