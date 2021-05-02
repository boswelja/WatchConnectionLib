import Publishing.configureMavenPublication

plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("signing")
}

android {
    compileSdk = Sdk.target

    defaultConfig {
        minSdk = Sdk.min
        targetSdk = Sdk.target
        consumerProguardFile("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

// Bundle sources with binaries
val androidSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].kotlin.name)
}
artifacts {
    archives(androidSourcesJar)
}

publishing {
    publications {
        create(
            "release",
            configureMavenPublication(
                "core",
                "Watch Connection Library core components",
                "https://github.com/boswelja/WatchConnectionLib/blob/main/core",
                project.configurations.implementation.get().allDependencies
            ) {
                artifact("$buildDir/outputs/aar/${project.name}-release.aar")
                artifact(androidSourcesJar)
            }
        )
    }
    repositories(Publishing.repositories)
}

// Create signing config
ext["signing.keyId"] = Publishing.signingKeyId
ext["signing.password"] = Publishing.signingPassword
ext["signing.secretKeyRingFile"] = Publishing.signingSecretKeyring
signing {
    sign(publishing.publications)
}

// Make publish task depend on assembleRelease
tasks.named("publishReleasePublicationToSonatypeRepository") {
    dependsOn("assembleRelease")
}
