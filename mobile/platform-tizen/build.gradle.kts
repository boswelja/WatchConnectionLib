import Publishing.repoUrlFor

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
    signing
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"
description = "Samsung Tizen support for Watch Connection Library"

kotlin {
    explicitApi()

    android {
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.mobile.mobileCore)
                api(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.proto)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(fileTree("libs"))
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

tasks {
    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)
    }
}

// Create signing config
ext["signing.keyId"] = Publishing.signingKeyId
ext["signing.password"] = Publishing.signingPassword
ext["signing.secretKeyRingFile"] = Publishing.signingSecretKeyring
signing {
    sign(publishing.publications)
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            artifact(tasks["javadocJar"])

            pom {
                name.set(this@afterEvaluate.name)
                description.set(this@afterEvaluate.description)
                url.set(repoUrlFor("mobile/platform-tizen"))
                licenses(Publishing.licenses)
                developers(Publishing.developers)
                scm(Publishing.scm)
            }
            repositories(Publishing.repositories)
        }
    }
}
