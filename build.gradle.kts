buildscript {
    val kotlinVersion = "1.4.32"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Create a new task to publish all releases
tasks.register("publishRelease") {
    dependsOn(
        ":core:publishReleasePublicationToSonatypeRepository",
        ":wearos:publishReleasePublicationToSonatypeRepository"
    )
}
