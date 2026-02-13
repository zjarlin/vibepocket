plugins {
    id("site.addzero.buildlogic.kmp.cmp-app")
    id("site.addzero.buildlogic.kmp.kmp-ksp-plugin")
    id("site.addzero.buildlogic.kmp.kmp-ktorfit")
}
// Ktorfit compiler plugin 兼容性配置
// Kotlin 2.3.x 需要 compilerPluginVersion = "2.3.3"
ktorfit {
    compilerPluginVersion.set("2.3.3")
}
dependencies {
    kspCommonMainMetadata(libs.site.addzero.ioc.processor.v2026)
}

kotlin {
    dependencies {
        implementation(libs.site.addzero.tool.json)
        implementation(libs.site.addzero.network.starter.v0)
        implementation(libs.site.addzero.ioc.core)
        implementation(libs.io.github.vinceglb.filekit.core)
        implementation(libs.io.github.vinceglb.filekit.dialogs)
        implementation(libs.io.github.vinceglb.filekit.dialogs.compose)
        implementation(libs.io.github.vinceglb.filekit.coil)
//       implementation("site.addzero:addzero-route-core:2025.09.29")
//        implementation("site.addzero:addzero-route-processor:2025.09.29")

        implementation(project(":lib:glass-components"))
        implementation(project(":lib:api-qqmusic"))
        implementation(project(":lib:api-suno"))
        implementation(project(":lib:api-netease"))


        implementation(libs.de.jensklingenberg.ktorfit.ktorfit.lib.light)
        implementation(libs.io.ktor.ktor.client.core)
        implementation(libs.io.ktor.ktor.client.cio)
        implementation(libs.io.ktor.ktor.client.content.negotiation)
        implementation(libs.io.ktor.ktor.serialization.kotlinx.json)
        implementation(libs.io.github.khubaibkhan4.mediaplayer.kmp)
        implementation(libs.eu.iamkonstantin.kotlin.gadulka)
        implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)
        implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.viewmodel.navigation3)

//        implementation(projects.shared)
    }
    sourceSets {

        jvmMain.dependencies {
            // 桌面端内嵌 Ktor 后端，无需单独部署 server
            implementation(projects.server)
            // ktor-server-core needed to access EmbeddedServer type from server module
            implementation(libs.io.ktor.ktor.server.core.jvm)
        }
        commonTest.dependencies {
            implementation(libs.io.kotest.kotest.property)
            implementation(libs.io.kotest.kotest.assertions.core)
            implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
        }
    }


}


