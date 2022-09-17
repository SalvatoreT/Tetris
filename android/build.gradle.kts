@file:Suppress("UnstableApiUsage", "OPT_IN_USAGE")

import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("android")
    id("org.jetbrains.compose")
    id("com.android.application")
}

version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":common"))

    val koinVersion = "3.2.0"
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("io.insert-koin:koin-android:$koinVersion")
}

android {
    namespace = "ds.tetris.android"

    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        // todo
    }

    buildTypes {
        debug {

        }
        release {
            signingConfig = signingConfigs["debug"]
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    buildOutputs.all {
        (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).run {
            if (name == "release") {
                outputFileName = "../../../../artifacts/android/tetris-${name}.apk"
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

