@file:Suppress("UnstableApiUsage", "OPT_IN_USAGE")

import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

version = "1.0"

kotlin {

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
                val koinVersion = "3.2.0"
                implementation("io.insert-koin:koin-core:$koinVersion")
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

compose {
    desktop {
        application {
            mainClass = "Main_desktopKt"

            nativeDistributions {
                outputBaseDir.set(projectDir.resolve("artifacts/desktop"))
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "TetrisMP"
                packageVersion = "1.0.0"

                windows {
                    // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                    upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
                }
            }
        }
    }
}
