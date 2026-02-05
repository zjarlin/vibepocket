plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.composeCompilerGradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.android.gradleApi)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
}
