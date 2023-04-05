@file:android.annotation.SuppressLint("ChromeOsAbiSupport")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "dev.mcd.chess.lc0"
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        minSdk = BuildSettings.minSdk

        ndk {
            abiFilters.clear()
            abiFilters += "arm64-v8a"
        }
    }

    kotlin {
        jvmToolchain(BuildSettings.jdkVersion)
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/native/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

val lczeroSrc = "src/main/native/lczero"
val unzipIncludes = project.tasks.register<DefaultTask>("unzipIncludes") {
    val destDir = file(lczeroSrc)
    outputs.upToDateWhen { destDir.exists() }
    doLast {
        project.copy {
            from(zipTree(file("$lczeroSrc.zip")))
            into(destDir)
        }
    }
}

tasks.named("clean").configure {
    doLast {
        project.delete(lczeroSrc)
    }
}

tasks.named("preBuild").configure {
    dependsOn(unzipIncludes)
}

dependencies {
    with(Versions) {
        api(project(":common"))
        api("com.jakewharton.timber:timber:$timber")
        api("com.google.dagger:hilt-android:$hilt")
        kapt("com.google.dagger:hilt-compiler:$hilt")
    }
}
