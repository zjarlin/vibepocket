plugins {
    id("site.addzero.conventions.compose-app")
//    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotest.property)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlinx.coroutines.test)
        }
    }

    dependencies {
        implementation("io.github.vinceglb:filekit-core:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")
        implementation("io.github.vinceglb:filekit-coil:0.12.0")

//       implementation("site.addzero:addzero-route-core:2025.09.29")
//        implementation("site.addzero:addzero-route-processor:2025.09.29")

        implementation(projects.glassComponents)
        implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")

        implementation(libs.khubaibkhan4.mediaplayer.kmp)
        implementation(libs.nav3)          // nav3 1.1.0-alpha02 拉入 compose 1.11-alpha / skiko 0.9.40，与当前 compose 1.10.0 / skiko 0.9.37.3 冲突
        implementation(libs.viewmodel.nav3) // 等 compose 升级到 1.11 稳定版再启用

//        implementation(projects.shared)
    }

}


