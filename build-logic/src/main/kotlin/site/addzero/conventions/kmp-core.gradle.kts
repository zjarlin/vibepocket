package site.addzero.conventions

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("site.addzero.conventions.kmp-convention")
}
kotlin {
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
//    iosArm64()
//    iosSimulatorArm64()
}
