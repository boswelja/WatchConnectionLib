import Publishing.configureMavenPublication
import Publishing.repoUrlFor

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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions.unitTests {
        isIncludeAndroidResources = true
    }
}

dependencies {
    api(projects.mobile.mobileCore)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.playservices)
    implementation(libs.play.services.wearable)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.core)
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
                "platform-wearos",
                "Wear OS support for Watch Connection Library",
                repoUrlFor("mobile/platform-wearos"),
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
