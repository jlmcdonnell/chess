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

    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
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

tasks.withType<Test> {
    useJUnitPlatform()
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
    api(project(":common"))

    api(libs.slf4j.nop)
    api(libs.jakewharton.timber)
    api(libs.kotlinx.coroutines.core)
    api(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.compiler)

    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
}
