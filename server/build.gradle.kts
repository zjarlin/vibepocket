import org.gradle.internal.execution.caching.CachingState.enabled

plugins {
    id("site.addzero.buildlogic.kmp.kmp-server")
    id("site.addzero.buildlogic.jvm.jvm-ksp-plugin")
}
ktor {
    openApi {
        enabled = false
    }
}

application {
    mainClass.set("site.addzero.vibepocket.ApplicationKt")
}


dependencies {
    ksp(libs.site.addzero.ioc.processor.v2026)
    implementation(libs.site.addzero.ioc.core)

    implementation(projects.shared)
    implementation(libs.site.addzero.ktor.banner)
    ksp(libs.org.babyfish.jimmer.jimmer.ksp)
    implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin)
    implementation(libs.org.xerial.sqlite.jdbc.v3)
    // ktor-server-openapi 和 ktor-server-routing-openapi 依赖编译器插件，暂时移除
    implementation(libs.io.ktor.ktor.server.swagger)
    implementation("site.addzero:tool-api-music-search:2026.01.20")
    implementation("site.addzero:tool-api-suno:2026.02.06")

    // Kotest property testing & assertions for server-side property tests
    testImplementation(libs.io.kotest.kotest.property)
    testImplementation(libs.io.kotest.kotest.assertions.core)
    testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    testImplementation("io.insert-koin:koin-test:4.2.0-RC1")
}
