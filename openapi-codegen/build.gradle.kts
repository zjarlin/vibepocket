plugins {
    id("site.addzero.buildlogic.kmp.kmp-core")
}
kotlin{
    jvm()

    dependencies {
        implementation(libs.com.google.devtools.ksp.symbol.processing.api)
        implementation(libs.io.swagger.parser.v3.swagger.parser)
        implementation(libs.com.squareup.kotlinpoet.v2)
        implementation(libs.com.squareup.kotlinpoet.ksp)
        testImplementation(libs.io.kotest.kotest.property)
        testImplementation(libs.io.kotest.kotest.assertions.core)
        testImplementation(libs.org.jetbrains.kotlin.kotlin.test)
    }
}
