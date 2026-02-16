
plugins {
    id("site.addzero.buildlogic.kmp.cmp-lib")
    id("site.addzero.buildlogic.kmp.kmp-coil")
    id("site.addzero.buildlogic.kmp.kmp-datetime")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)
        }
    }
}

