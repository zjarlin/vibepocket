plugins {
    id("site.addzero.conventions.compose-app")
//    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    dependencies {
        implementation("io.github.vinceglb:filekit-core:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs:0.12.0")
        implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")
        implementation("io.github.vinceglb:filekit-coil:0.12.0")

//       implementation("site.addzero:addzero-route-core:2025.09.29")
//        implementation("site.addzero:addzero-route-processor:2025.09.29")

        implementation("site.addzero:compose-native-component-glass:2025.12.22")
        implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")
        implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")

        implementation(libs.khubaibkhan4.mediaplayer.kmp)
//        implementation(libs.androidx.navigation.compose)
        implementation(libs.nav3)
        implementation(libs.viewmodel.nav3)

//        implementation(projects.shared)
    }

}


