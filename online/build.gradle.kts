plugins {
    id("com.android.library")
    id("kotlinx-serialization")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
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
    with(Versions) {
        api(project(":common"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
        api("com.github.bhlangonijr:chesslib:$chessLib")
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")
        api("javax.inject:javax.inject:1")

        // Ktor
        api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        api("io.ktor:ktor-client-core:$ktor")
        api("io.ktor:ktor-client-okhttp:$ktor")
        api("io.ktor:ktor-client-content-negotiation:$ktor")
        api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        api("io.ktor:ktor-client-logging:$ktor")

        // Hilt
        api("androidx.hilt:hilt-navigation-compose:$hiltNavigationCompose")
        api("com.google.dagger:hilt-android:$hilt")
        kapt("com.google.dagger:hilt-compiler:$hilt")

        testImplementation("io.ktor:ktor-client-cio:$ktor")
        testImplementation("junit:junit:$junit4")
    }
}
