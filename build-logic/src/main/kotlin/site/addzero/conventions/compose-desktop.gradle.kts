package site.addzero.conventions
import org.gradle.accessors.dm.LibrariesForLibs

import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val libs = the<LibrariesForLibs>()

plugins {
    id("site.addzero.conventions.compose-lib")
}

kotlin {

    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "site.addzero.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "site.addzero"

            packageVersion = "1.0.0"
        }
    }
}


