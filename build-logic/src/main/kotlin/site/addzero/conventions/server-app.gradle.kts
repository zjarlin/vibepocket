@file:Suppress("UnstableApiUsage")

package site.addzero.conventions

import gradle.kotlin.dsl.accessors._d91e95f7e6eb91f674b365c3c524c1b9.ksp
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    application
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

application {
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.findLibrary("koin-annotations").get())
    implementation(libs.findLibrary("koin-ktor").get())
    implementation(libs.findLibrary("koin-loggerSlf4j").get())
    implementation(libs.findLibrary("logback").get())
    implementation(libs.findLibrary("ktor-serverCore").get())
    implementation(libs.findLibrary("ktor-serverNetty").get())
    implementation(libs.findLibrary("ktor-serverStatusPages").get())
    ksp(libs.findLibrary("koin-ksp-compiler").get())
    testImplementation(libs.findLibrary("ktor-serverTestHost").get())
    testImplementation(libs.findLibrary("kotlin-testJunit").get())
}
