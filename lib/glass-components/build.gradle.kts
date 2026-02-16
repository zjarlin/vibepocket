plugins {
    id("site.addzero.buildlogic.kmp.cmp-lib")
}

kotlin {
    dependencies {
        implementation(libs.io.github.kyant0.shapes)
        implementation("io.github.kyant0:backdrop:2.0.0-alpha03")


    }
}
