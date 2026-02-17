plugins {
    id("site.addzero.buildlogic.kmp.cmp-app")
    id("site.addzero.buildlogic.kmp.kmp-ksp-plugin")
    id("site.addzero.buildlogic.kmp.kmp-ktorfit")
    id("site.addzero.buildlogic.kmp.kmp-koin")
    id("site.addzero.buildlogic.kmp.kmp-filekit")
    id("site.addzero.buildlogic.kmp.kmp-json-withtool")

    id("site.addzero.buildlogic.kmp.kmp-ktor-client")
}
// Ktorfit compiler plugin 兼容性配置
// Kotlin 2.3.x 需要 compilerPluginVersion = "2.3.3"
//ktorfit {
//    compilerPluginVersion.set("2.3.3")
//}
dependencies {
    kspCommonMainMetadata(libs.site.addzero.ioc.processor)
//    kspCommonMainMetadata("site.addzero:apiprovider-processor:2025.09.30")
//    kspCommonMainMetadata(project(":openapi-codegen"))
//    add("kspJvm", project(":openapi-codegen"))
}

//ksp {
//    arg("openapi.spec", "$projectDir/src/commonMain/resources/openapi.json")
//    arg("openapi.package", "site.addzero.vibepocket.api.generated")
//}

kotlin {
    dependencies {
        implementation(libs.site.addzero.ioc.core)
        implementation(libs.site.addzero.network.starter)
//       implementation("site.addzero:addzero-route-core:2025.09.29")
//        implementation("site.addzero:addzero-route-processor:2025.09.29")
        implementation(project(":lib:glass-components"))
//        implementation(project(":checkouts:shadcn-ui-kmp"))
//        implementation("io.github.kyant0:backdrop:2.0.0-alpha03")
        implementation(project(":lib:shadcn-ui-kmp"))

//        implementation(project(":lib:api-qqmusic"))
        implementation(project(":lib:api-suno"))

        implementation("site.addzero:api-netease:2026.02.17")

//        implementation(libs.de.jensklingenberg.ktorfit.ktorfit.lib.light)
        implementation(libs.io.github.khubaibkhan4.mediaplayer.kmp)


//        implementation(libs.eu.iamkonstantin.kotlin.gadulka)
//        implementation(projects.shared)
    }
    sourceSets {
        jvmMain.dependencies {
            implementation(libs.io.ktor.ktor.server.netty.jvm)

            // 桌面端内嵌 Ktor 后端，无需单独部署 server
            implementation(projects.server)
        }
    }

}


