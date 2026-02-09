@file:Suppress("UnstableApiUsage")

package site.addzero.conventions

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.insert-koin.compiler.plugin")

    application
}

val libs = the<LibrariesForLibs>()

application {
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.io.insert.koin.koin.annotations)
    implementation(libs.io.insert.koin.koin.ktor)
    implementation(libs.io.insert.koin.koin.logger.slf4j)
    implementation(libs.ch.qos.logback.logback.classic)
    implementation(libs.io.ktor.ktor.server.core.jvm)
    implementation(libs.io.ktor.ktor.server.netty.jvm)
    implementation(libs.io.ktor.ktor.server.status.pages)
    testImplementation(libs.io.ktor.ktor.server.test.host.jvm)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test.junit)
}
