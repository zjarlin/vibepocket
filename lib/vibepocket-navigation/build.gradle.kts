plugins {
    id("site.addzero.buildlogic.kmp.cmp-lib")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":lib:shadcn-ui-kmp"))
            implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)
        }
    }
}
