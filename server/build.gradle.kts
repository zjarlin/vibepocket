plugins {
    id("site.addzero.buildlogic.kmp.kmp-ktor-server")
    id("site.addzero.buildlogic.jvm.jimmer")
    id("site.addzero.buildlogic.jvm.jvm-koin")
    id("site.addzero.buildlogic.jvm.jvm-json-withtool")
    id("site.addzero.buildlogic.jvm.jvm-ksp-plugin")
}
application {
    mainClass.set("site.addzero.vibepocket.ApplicationKt")
}

dependencies {
    ksp("site.addzero:entity2iso-processor:2026.02.22")

    implementation("site.addzero:tool-api-music-search:2026.01.20")
    implementation("site.addzero:tool-api-suno:2026.02.06")

    implementation(libs.site.addzero.ioc.core)
    ksp(libs.site.addzero.ioc.processor)


    implementation(projects.shared)
    implementation(libs.org.xerial.sqlite.jdbc.v3)
    implementation(libs.org.postgresql.postgresql)
    // ktor-server-openapi 和 ktor-server-routing-openapi 依赖编译器插件，与 Kotlin 2.3.20-Beta2 不兼容，已改用静态 OpenAPI 规范文件 + KSP codegen 方案
    // Kotest property testing & assertions for server-side property tests
    testImplementation(libs.io.kotest.kotest.property)
    testImplementation(libs.io.kotest.kotest.assertions.core)
    testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
}
