@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://androidx.dev/snapshots/builds/9664109/artifacts/repository")
        }
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
rootProject.name = "Chess"
include(
    "app",
    "common",
    "engine-stockfish",
    "engine-lc0",
    "online",
    "baselineprofile",
)
