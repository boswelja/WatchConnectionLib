plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"
description = "Watch Connection Library internal common components"

kotlin {
    explicitApi()

    android {
        publishLibraryVariants("release")
    }
    iosArm64()
    watchosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies { }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}

android {
    compileSdk = Sdk.target
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Sdk.min
        targetSdk = Sdk.target
        consumerProguardFile("proguard-rules.pro")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}
