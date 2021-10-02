import Publishing.repoUrlFor

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"
description = "Smartwatch components for Watch Connection Library"

kotlin {
    explicitApi()

    android {
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(projects.common.common)
                implementation(projects.common.internal)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.play.services.wearable)
                implementation(libs.kotlinx.coroutines.playservices)
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
                url.set(repoUrlFor("wear"))
                licenses(Publishing.licenses)
                developers(Publishing.developers)
                scm(Publishing.scm)
            }
            repositories(Publishing.repositories)
        }
    }
}
