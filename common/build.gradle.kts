import Publishing.repoUrlFor

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    signing
}

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
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies { }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.mockk.core)
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.koverVerify {
    rule {
        name = "80% Coverage"
        bound {
            minValue = 80
            valueType = kotlinx.kover.api.VerificationValueType.COVERED_LINES_PERCENTAGE
        }
    }
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"
description = "Watch Connection Library common components"

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
            pom {
                name.set(this@afterEvaluate.name)
                description.set(this@afterEvaluate.description)
                url.set(repoUrlFor("common"))
                licenses(Publishing.licenses)
                developers(Publishing.developers)
                scm(Publishing.scm)
            }
            repositories(Publishing.repositories)
        }
    }
}
