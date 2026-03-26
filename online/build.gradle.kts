plugins {
    id("com.android.library")
    id("kotlinx-serialization")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "dev.mcd.chess.online"
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        minSdk = BuildSettings.minSdk
    }

    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
    }
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
}

dependencies {
    api(project(":common"))
    api(libs.kotlinx.coroutines.core)
    api(libs.chesslib)
    api(libs.kotlinx.serialization.json)
    api(libs.javax.inject)

    // Ktor
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.client.core)
    api(libs.ktor.client.okhttp)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.logging)

    // Hilt
    api(libs.androidx.hilt.navigation.compose)
    api(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)

    testImplementation(libs.ktor.client.cio)
    testImplementation(libs.junit.junit)
}
