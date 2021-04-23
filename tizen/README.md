# Tizen

Provides an interface for connecting to Tizen smartwatches via Samsung's Accessory SDK.

## Usage

Under the hood, this plugin still uses Samsung's Accessory SDK, so you'll need to be familiar with that first. Find out more here: https://developer.samsung.com/galaxy-accessory/overview.html

To use this plugin, you'll want to create an instance of `TizenPlatform`. The result should look something like this:
```kotlin
val tizenPlatform = TizenPlatform(context)
```

At this stage, you still need to create your own Accessory Service Provider xml file.
Make sure to declare 'message' as a supported feature, and point `serviceImpl` to `TizenAccessoryAgent`. You should have something like the this:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <application name="@string/app_name">
        <serviceProfile
            serviceImpl="com.boswelja.watchconnection.tizen.TizenAccessoryAgent"
            role="provider"
            name="WatchConnectionLib"
            id="/watchconnection/tizen"
            version="1.0"
            serviceLimit="any"
            serviceTimeout="10">
            <supportedTransports>
                <transport type="TRANSPORT_BT"/>
            </supportedTransports>
            <supportedFeatures>
                <feature type="message"/>
            </supportedFeatures>
        </serviceProfile>
    </application>
</resources>
```

Find out more via [Samsung's programming guide](https://developer.samsung.com/galaxy-accessory/programming-guide.html).

You'll still want to use Samsung's Accessory SDK in your Tizen app. Currently, this module only supports `SAMessage`, as well as getting watches via `findPeerAgents()`.

This module does not currently differentiate between watches with and without your app installed. Both `allWatches()` and `watchesWithApp()` return the same Flow.

To support messages, you'll need to prefix anything you send from your watch app with a string to identify it, followed by `|` to indicate where the identifier ends and the data starts.
