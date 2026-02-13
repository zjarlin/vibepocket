plugins {
    id("site.addzero.buildlogic.kmp.kmp-ktorfit")
    id("site.addzero.buildlogic.kmp.kmp-ktor-client")
}
kotlin {
    dependencies {
        implementation(libs.site.addzero.network.starter.v0)
    }
}
