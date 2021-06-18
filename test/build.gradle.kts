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
    implementation(projects.core)
    implementation(projects.wearos)
    implementation(projects.tizen)

    testImplementation(libs.androidx.arch.test)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
    testImplementation(libs.mockk.core)
    testImplementation(libs.robolectric)
}
