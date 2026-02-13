plugins {
    id("site.addzero.buildlogic.kmp.kmp-ktorfit")
    id("site.addzero.buildlogic.kmp.kmp-ktor-client")
    id("site.addzero.buildlogic.kmp.kmp-json-withtool")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.site.addzero.network.starter.v0)
            api(projects.lib.apiMusicSpi)
        }
        commonTest.dependencies {
            implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
        }
    }
}
