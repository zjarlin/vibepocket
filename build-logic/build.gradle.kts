plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.org.jetbrains.kotlin.kotlin.gradle.plugin)
    implementation(libs.org.jetbrains.kotlin.compose.compiler.gradle.plugin)
    implementation(libs.com.android.tools.build.gradle)
    implementation(libs.com.google.devtools.ksp.com.google.devtools.ksp.gradle.plugin)
    implementation(libs.io.insert.koin.koin.compiler.gradle.plugin)
    implementation(libs.org.jetbrains.compose.compose.gradle.plugin)

//   implementation("io.insert-koin:koin-compiler-plugin:0.3.0")
}
