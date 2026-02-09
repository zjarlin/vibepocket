package site.addzero.conventions
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()


plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.org.jetbrains.kotlin.kotlin.test)
        }
    }
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

