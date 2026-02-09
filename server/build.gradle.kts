plugins {
    id("site.addzero.conventions.server-app")
    alias(libs.plugins.koin.compiler)
//    id("io.insert-koin.compiler.plugin")
}

group = "site.addzero.vibepocket"
version = "1.0.0"

application {
    mainClass.set("site.addzero.vibepocket.ApplicationKt")
}

dependencies {
//    implementation(projects.shared)
    implementation("site.addzero:tool-api-music-search:2026.01.20")
    implementation("site.addzero:tool-api-suno:2026.02.06")
}
