# Core

Provides all the base classes, interfaces and enums for implementing platforms, as well as a class to more easily manage multiple platforms.

## Usage

### Receiving Messages

#### Registered receivers

You can register to receive messages while your app is running.

To do this, create a `MessageListener` and call `WatchPlatformManager.addMessageListener` to register the listener.
The second is to create a class extending `MessageReceiver` and register 
Note you should call `WatchPlatformManager.removeMessageListener` when you no longer need your message listener.

Using this method would look something like this:

```kotlin
val messageListener = object : MessageListener {
    override fun onMessageReceived(
        sourceWatchId: UUID,
        message: String,
        data: ByteArray?
    ) {
        // Handle the received message here
    }
}

// Add the message listener
watchPlatformManager.addMessageListener(messageListener)

// Remove the message listener when no longer needed
watchPlatformManager.removeMessageListener(messageListener)
```

#### Manifest-declared receivers

You can also register to receive messages via a manifest-declared broadcast receiver.

To do this, create a class extending `MessageReceiver` and register it in your app manifest with an intent filter for the action `com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED`.

Your resulting class should look like this:
```kotlin
class WatchMessageReceiver : MessageReceiver() {
    override suspend fun onMessageReceived(
        sourceWatchId: UUID,
        message: String,
        data: ByteArray?
    ) {
        // Handle message here
    }
}
```

And in your manifest:
```xml
<manifest>
    <application>
        <receiver
            android:name=".WatchMessageReceiver"
            android:exported="false"> <!-- Specify exported=false here to follow best practices -->
            <intent-filter>
                <action android:name="com.boswelja.watchconnection.messages.ACTION_MESSAGE_RECEIVED" />
            </intent-filter>
        </service>
    </application>
</manifest>

```
