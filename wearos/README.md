# Wear OS

Provides an interface for conecting to Wear OS smartwatches via Google's Wearable support library.

## Usage

Under the hood, this plugin still uses Google's Wearable Support Library, so you'll need to be familiar with that first. Find out more here: https://developer.android.com/training/wearables/apps

To use this plugin, you'll want to create an instance of `WearOSConnectionhandler`, providing your app capability identifier to the constructor (see [here](https://developer.android.com/training/wearables/apps/independent-vs-dependent#detecting-your-app) for more info). The result should look something like this: 
```kotlin
val wearOSConnectionHandler = WearOSConnectionHandler(context, MY_APP_CAPABILITY)
```

You'll still want to use Google's Wearable Support APIs in your Wear OS app. Currently, this module only supports `MessageClient`, as well as getting watches with your defined capability advertised.
