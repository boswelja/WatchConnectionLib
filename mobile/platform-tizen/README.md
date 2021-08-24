# Tizen

Provides an interface for connecting to Tizen smartwatches via Samsung's Accessory SDK.

### This platform is currently EXPERIMENTAL and may be prone to crashes and other errors

## Usage

Under the hood, this plugin still uses Samsung's Accessory SDK, so you'll need to be familiar with that first. Find out more here: https://developer.samsung.com/galaxy-accessory/overview.html

To use this plugin, you'll want to create an instance of `TizenPlatform`. The result should look something like this:
```kotlin
val tizenPlatform = TizenPlatform(context)
```

The Accessory application name defaults to `@string/app_name`, however you can override it by setting `@string/accessory_app_name` string yourself.

Find out more via [Samsung's programming guide](https://developer.samsung.com/galaxy-accessory/programming-guide.html).

You'll still want to use Samsung's Accessory SDK in your Tizen app. Currently, this module only supports `SAMessage`, as well as getting watches via `findPeerAgents()`.

This module does not currently differentiate between watches with and without your app installed. Both `allWatches()` and `watchesWithApp()` return the same Flow.

To support messages, you'll need to prefix anything you send from your watch app with a string to identify it, followed by `|` to indicate where the identifier ends and the data starts.
