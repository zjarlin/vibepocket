@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("site.addzero.conventions.compose-app")
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    dependencies {
//               implementation("io.github.shadmanadman:kmpShaPlayer:1.0.2")
//                implementation("io.github.kdroidfilter:composemediaplayer:0.8.7")
        implementation("io.github.vinceglb:filekit-core:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")
        implementation("io.github.vinceglb:filekit-coil:0.12.0")

        implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")

        implementation(libs.khubaibkhan4.mediaplayer.kmp)
        implementation(projects.shared)
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "site.addzero.vibepocket"
    defaultConfig {
        applicationId = "site.addzero.vibepocket"
        versionCode = 1
        versionName = "1.0"
    }
}

compose.desktop {
    application {
        mainClass = "site.addzero.vibepocket.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "site.addzero.vibepocket"
            packageVersion = "1.0.0"
        }
    }
}
