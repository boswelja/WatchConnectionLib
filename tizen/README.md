# Tizen

Provides an interface for connecting to Tizen smartwatches via Samsung's Accessory SDK.

## Usage

Under the hood, this plugin still uses Samsung's Accessory SDK, so you'll need to be familiar with that first. Find out more here: https://developer.samsung.com/galaxy-accessory/overview.html

To use this plugin, you'll want to create an instance of `TizenConnectionHandler`. The result should look something like this:
```kotlin
val tizenConnectionHandler = TizenConnectionHandler(context)
```

You'll still want to use Samsung's Accessory SDK in your Tizen app. Currently, this module only supports `SAMessage`, as well as getting watches via `findPeerAgents()`.
This module does not currently support differentiating between watches with and without your app installed. Both `allWatches()` and `watchesWithApp()` return the same Flow.
