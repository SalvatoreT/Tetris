@file:Suppress("UnstableApiUsage", "OPT_IN_USAGE")

import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }

    android()

    jvm("desktop")

    js(IR) {
        browser {
        }
    }

    sourceSets {

        val koinVersion = "3.2.0"

        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.github.aakira:napier:2.6.1")
                implementation("com.soywiz.korlibs.korau:korau:2.2.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val jsMain by getting {
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.8.0")
                implementation("androidx.appcompat:appcompat:1.5.0")
                implementation("androidx.activity:activity-compose:1.5.1")
                implementation("io.insert-koin:koin-android:$koinVersion")
            }
        }

        all {
            languageSettings {
                optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
            }
        }
    }
}

android {
    sourceSets {
        val main by getting
        main.assets.setSrcDirs(listOf("src/commonMain/resources"))
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.10.0"
        nodeVersion = "16.0.0"
    }
}


// TODO: remove when https://youtrack.jetbrains.com/issue/KT-50778 fixed
project.tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java).configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xir-dce-runtime-diagnostic=log"
    )
}
