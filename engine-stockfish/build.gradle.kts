@file:Suppress("UnstableApiUsage")
@file:android.annotation.SuppressLint("ChromeOsAbiSupport")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "dev.mcd.chess.engine"
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        minSdk = BuildSettings.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

val stockfishSrc = "src/main/native/stockfish"
val unzipIncludes = project.tasks.register<DefaultTask>("unzipIncludes") {
    val destDir = file(stockfishSrc)
    outputs.upToDateWhen { destDir.exists() }
    doLast {
        project.copy {
            from(zipTree(file("$stockfishSrc.zip")))
            into(destDir)
        }
    }
}

tasks.named("clean").configure {
    doLast {
        project.delete(stockfishSrc)
    }
}

tasks.named("preBuild").configure {
    dependsOn(unzipIncludes)
}

dependencies {
    with(Versions) {
        api(project(":common"))

        api("org.slf4j:slf4j-nop:$slf4j")
        api("com.jakewharton.timber:timber:$timber")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
        api("com.google.dagger:hilt-android:$hilt")
        kapt("com.google.dagger:hilt-compiler:$hilt")

        testImplementation("junit:junit:$junit4")
        testImplementation("io.mockk:mockk:$mockk")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")
    }
}
