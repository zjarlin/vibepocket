plugins {
    id("site.addzero.buildlogic.kmp.kmp-core")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.de.jensklingenberg.ktorfit.ktorfit.lib.light)
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

kotlin {



    dependencies {
        implementation(libs.de.jensklingenberg.ktorfit.ktorfit.lib.light)
        implementation(libs.io.ktor.ktor.client.core)
        implementation(libs.io.ktor.ktor.client.content.negotiation)
        implementation(libs.io.ktor.ktor.serialization.kotlinx.json)
        implementation(libs.site.addzero.network.starter.v0)
    }
}
