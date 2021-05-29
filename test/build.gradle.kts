plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Sdk.target

    defaultConfig {
        minSdk = Sdk.min
        targetSdk = Sdk.target

        applicationId = "com.boswelja.watchconnection.test"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }

    // Workaround for old Accessory SDK
    testOptions.unitTests.all {
        it.jvmArgs("-noverify")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":wearos"))
    implementation(project(":tizen"))

    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("androidx.test:core-ktx:1.4.0-beta01")
    testImplementation("androidx.test.ext:junit-ktx:1.1.3-beta01")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.strikt:strikt-core:0.31.0")
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("org.robolectric:robolectric:4.5.1")
}
