import java.io.File

rootProject.name = rootDir.name
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("site.addzero.gradle.plugin.repo-buddy") version "+"
}

fun File.findGradleProjectDirs(): List<File> {
    val ignoredDirs = setOf(".git", ".gradle", ".idea", "build", "out", "target")
    val children = listFiles().orEmpty()
        .filter { it.isDirectory && it.name !in ignoredDirs }

    val result = mutableListOf<File>()
    if (resolve("build.gradle.kts").exists()) {
        result += this
    }
    children.forEach { child ->
        result += child.findGradleProjectDirs()
    }
    return result
}

fun File.toGradleModulePath(root: File): String {
    val relativePath = relativeTo(root).invariantSeparatorsPath
    return ":${relativePath.replace("/", ":")}"
}

rootDir.findGradleProjectDirs()
    .filter { it != rootDir }
    .filterNot { it.name == "buildSrc" }
    .sortedBy { it.relativeTo(rootDir).invariantSeparatorsPath }
    .forEach { moduleDir ->
        include(moduleDir.toGradleModulePath(rootDir))
    }
