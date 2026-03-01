plugins {
    id("site.addzero.buildlogic.jvm.kotlin-convention")
    id("site.addzero.buildlogic.jvm.jimmer")
    id("site.addzero.buildlogic.jvm.jvm-koin")
}

dependencies {
    implementation(projects.lib.starterSpi)
    implementation(libs.io.ktor.ktor.server.core)
    implementation(libs.io.insert.koin.koin.ktor)
    implementation(libs.org.xerial.sqlite.jdbc.v3)
    implementation(libs.org.postgresql.postgresql)
    implementation(libs.site.addzero.ioc.core)
}
