plugins {
    id("site.addzero.conventions.server-app")
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.com.google.devtools.ksp.com.google.devtools.ksp.gradle.plugin)
    alias(libs.plugins.enaium.jimmer.gradle)
    alias(libs.plugins.ktor.plugin)
}


// Ktor OpenAPI 编译器插件与 Kotlin 2.3.20-Beta2 不兼容
// (NoSuchMethodError: LOCAL_FUNCTION_FOR_LAMBDA)
// 保留 ktor plugin 用于 fatJar/docker，仅禁用 openApi 代码生成
ktor {
    openApi {
        enabled = false
    }
}

group = "site.addzero.vibepocket"
version = "1.0.0"

application {
    mainClass.set("site.addzero.vibepocket.ApplicationKt")
}

dependencies {
    ksp(libs.ioc.processor)
    implementation(libs.ioc.core)

    implementation(projects.shared)
    implementation(libs.ktor.banner)
    ksp(libs.jimmer.ksp)
    implementation(libs.jimmer.sql.kotlin)
    implementation(libs.sqlite.jdbc)
    // ktor-server-openapi 和 ktor-server-routing-openapi 依赖编译器插件，暂时移除
    implementation(libs.io.ktor.ktor.server.swagger)
    implementation("site.addzero:tool-api-music-search:2026.01.20")
    implementation("site.addzero:tool-api-suno:2026.02.06")
}
