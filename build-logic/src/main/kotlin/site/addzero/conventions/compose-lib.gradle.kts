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
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.ui)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.uiToolingPreview)
        implementation(libs.androidx.lifecycle.viewmodelCompose)
        implementation(libs.androidx.lifecycle.runtimeCompose)
        implementation(libs.material.icons.extended)

    }

}

