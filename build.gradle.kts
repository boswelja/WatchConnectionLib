buildscript {
    val kotlinVersion = "1.5.31"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0-alpha04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${libs.versions.hilt.get()}")
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    id("org.jetbrains.kotlinx.kover") version "0.4.1"
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    repositories {
        google()
        mavenCentral()
    }
    detekt {
        config = files("$rootDir/config/detekt/detekt.yml")
        source = files("src")
        buildUponDefaultConfig = true
        parallel = true
    }

    tasks.koverVerify {
        rule {
            name = "75% Coverage"
            bound {
                minValue = 75
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_LINES_PERCENTAGE
            }
        }
    }
}
group = Publishing.groupId
version = Publishing.version ?: "0.1.0"

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl
                .set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(Publishing.ossrhUsername)
            password.set(Publishing.ossrhPassword)
        }
    }
}
