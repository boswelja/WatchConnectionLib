# Wear OS

Provides an interface for connecting to Wear OS smartwatches via Google's Wearable support library.

## Usage

Under the hood, this plugin still uses Google's Wearable Support Library, so you'll need to be familiar with that first. Find out more here: https://developer.android.com/training/wearables/apps

To use this plugin, you'll want to create an instance of `WearOSPlatform`.
You'll need to provide your app capability identifier to the constructor (see [here](https://developer.android.com/training/wearables/apps/independent-vs-dependent#detecting-your-app) for more info), as well as a list of capabilities you'd like to detect when calling `getCapabilitiesFor()`
The result should look something like this:
```kotlin
val wearOSPlatform = WearOSPlatform(context, MY_APP_CAPABILITY)
```

You'll still want to use Google's Wearable Support APIs in your Wear OS app. Currently, this module only supports `MessageClient`, as well as getting watches with your defined capability advertised.
