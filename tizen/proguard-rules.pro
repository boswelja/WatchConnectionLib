# Keep classes
-keep class com.boswelja.watchconnection.tizen.TizenPlatform
-keep class com.boswelja.watchconnection.tizen.TizenAccessoryAgent

# Keep objects
-keep class com.boswelja.watchconnection.tizen.Messages

# Keep Samsung Accessory SDK
-keepclassmembers class com.samsung.** { *;}
-keep class com.samsung.** { *; }
-dontwarn com.samsung.**
