package site.addzero.conventions

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
//    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val pkg = "site.addzero"
val libs = the<LibrariesForLibs>()


//target {
//    compilerOptions {
//           jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
//    }
//}
android {
    namespace = pkg
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = pkg
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)

//    implementation("androidx.compose.material3:material3:1.4.0")
//    implementation(compose.preview)
    // implementation(libs.androidx.room.sqlite.wrapper)
//    implementation("io.insert-koin:koin-android:3.2.0")
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.testExt.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}
