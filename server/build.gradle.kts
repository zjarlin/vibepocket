plugins {
    id("site.addzero.conventions.server-app")
}

group = "site.addzero.vibepocket"
version = "1.0.0"

application {
    mainClass.set("site.addzero.vibepocket.ApplicationKt")
}

dependencies {
    implementation(projects.shared)
}
