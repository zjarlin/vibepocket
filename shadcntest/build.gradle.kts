plugins {
    id("site.addzero.buildlogic.kmp.cmp-app")
    id("site.addzero.buildlogic.kmp.kmp-ksp-plugin")
    id("site.addzero.buildlogic.kmp.kmp-ktorfit")
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

kotlin {
    dependencies {
        implementation("io.github.derangga:shadcn-ui-kmp:0.2.0")


        implementation(libs.io.github.khubaibkhan4.mediaplayer.kmp)


        implementation(libs.eu.iamkonstantin.kotlin.gadulka)
        implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)
        implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.viewmodel.navigation3)

//        implementation(projects.shared)
    }
    sourceSets {

        jvmMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.io.kotest.kotest.property)
            implementation(libs.io.kotest.kotest.assertions.core)
            implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
        }
    }


}


