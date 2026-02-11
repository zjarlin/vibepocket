plugins {
    id("site.addzero.conventions.compose-app")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.com.google.devtools.ksp.com.google.devtools.ksp.gradle.plugin)
    alias(libs.plugins.de.jensklingenberg.ktorfit.ktorfit.lib.light)
//    alias(libs.plugins.androidMultiplatformLibrary)
}

// Ktorfit compiler plugin 兼容性配置
// Kotlin 2.3.x 需要 compilerPluginVersion = "2.3.3"
ktorfit {
    compilerPluginVersion.set("2.3.3")
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.io.kotest.kotest.property)
            implementation(libs.io.kotest.kotest.assertions.core)
            implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
        }
    }

    dependencies {
        implementation(libs.filekit.core)
        implementation(libs.filekit.dialogs)
        implementation(libs.filekit.dialogs.compose)
        implementation(libs.filekit.coil)

//       implementation("site.addzero:addzero-route-core:2025.09.29")
//        implementation("site.addzero:addzero-route-processor:2025.09.29")

        implementation(projects.glassComponents)
        implementation(libs.de.jensklingenberg.ktorfit.ktorfit.lib.light)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.cio)
        implementation(libs.io.ktor.ktor.client.content.negotiation)
        implementation(libs.io.ktor.ktor.serialization.kotlinx.json)

        implementation(libs.io.github.khubaibkhan4.mediaplayer.kmp)
        implementation(libs.gadulka)
        implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)          // nav3 1.1.0-alpha02 拉入 compose 1.11-alpha / skiko 0.9.40，与当前 compose 1.10.0 / skiko 0.9.37.3 冲突
        implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.viewmodel.navigation3) // 等 compose 升级到 1.11 稳定版再启用

//        implementation(projects.shared)
    }

}


