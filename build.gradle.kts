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

plugins {
    id("io.codearte.nexus-staging") version "0.30.0"
}

nexusStaging {
    packageGroup = Publishing.groupId
    stagingProfileId = Publishing.stagingProfileId
    username = Publishing.ossrhUsername
    password = Publishing.ossrhPassword
}