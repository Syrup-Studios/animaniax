pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("dev.kikugie.loom-back-compat") version "0.4.3"
}

rootProject.name = "animaniax"

val loomVersion = extensions.extraProperties.get("loomx.loom_version").toString()
gradle.beforeProject {
    extensions.extraProperties.set("loomx.loom_version", loomVersion)
}

stonecutter {
    create(rootProject) {
        version("1.21.1-fabric", "1.21.1").buildscript("build.fabric.gradle.kts")
        version("1.21.1-neoforge", "1.21.1").buildscript("build.neoforge.gradle.kts")
        vcsVersion = "1.21.1-fabric"
    }
}
