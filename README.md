# Watch Connection Library

A kotlin-first Android library to provide a shared API for multiple smartwatch platforms. You can find the latest version from the [Releases section](https://github.com/boswelja/WatchConnectionLib/releases)

## Modules

### [Core](https://github.com/boswelja/WatchConnectionLib/blob/main/core)

### [Wear OS](https://github.com/boswelja/WatchConnectionLib/blob/main/wearos)

### [Tizen](https://github.com/boswelja/WatchConnectionLib/blob/main/tizen)

### More coming soon

## Usage

### This library is still in development, APIs are subject to change without notice

Add module dependencies for platforms you want to support:
```kotlin
dependencies {
    // Wear OS support
    implementation("io.github.boswelja.watchconnection:wearos:$watchConnectionVersion")
    // Tizen support
    implementation("io.github.boswelja.watchconnection:tizen:$watchConnectionVersion")
}
```

Next, you'll want to create an instance of `WatchConnectionClient` and pass in all your desired `PlatformConnectionHandler`s provided by your selected modules (see each modules documentation for more info in creating their respective platform handlers). The result should look something like this:
```kotlin
val connectionClient = WatchConnectionClient(
    wearOSConnectionHandler,
    tizenConnectionHandler
)
```
