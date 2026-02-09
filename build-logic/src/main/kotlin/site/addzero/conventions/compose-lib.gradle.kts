package site.addzero.conventions

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("site.addzero.conventions.kmp-core")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
val libs = the<LibrariesForLibs>()

val pkg = "site.addzero"
kotlin {
    dependencies {
        implementation(libs.org.jetbrains.compose.runtime.runtime)
        implementation(libs.org.jetbrains.compose.foundation.foundation)
        implementation(libs.org.jetbrains.compose.material3.material3)
        implementation(libs.org.jetbrains.compose.ui.ui)
        implementation(libs.org.jetbrains.compose.components.components.resources)
        implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
        implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.viewmodel.compose)
        implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.runtime.compose)
        implementation(libs.org.jetbrains.compose.material.material.icons.extended)

    }

}

