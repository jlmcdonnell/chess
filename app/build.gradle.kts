@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android {
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        namespace = "dev.mcd.chess"
        applicationId = "dev.mcd.chess"
        minSdk = BuildSettings.minSdk
        targetSdk = BuildSettings.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }
    kotlin {
        jvmToolchain(BuildSettings.jdkVersion)
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/**"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    with(Versions) {
        // Projects
        implementation(project(":stockfish"))
        implementation(project(":engine-common"))

        // Core
        implementation("org.slf4j:slf4j-nop:$slf4j")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
        implementation("androidx.core:core-ktx:$coreKtx")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleRuntimeKtx")

        // Compose
        implementation("androidx.compose.ui:ui:$compose")
        implementation("androidx.compose.foundation:foundation:$compose")
        implementation("androidx.compose.material:material:$compose")
        implementation("androidx.compose.material:material-icons-extended:$compose")
        debugImplementation("androidx.compose.ui:ui-tooling:$compose")

        // Ktor
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        implementation("io.ktor:ktor-client-core:$ktor")
        implementation("io.ktor:ktor-client-okhttp:$ktor")
        implementation("io.ktor:ktor-client-content-negotiation:$ktor")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        implementation("io.ktor:ktor-client-logging:$ktor")

        // Orbit
        implementation("org.orbit-mvi:orbit-core:$orbit")
        implementation("org.orbit-mvi:orbit-viewmodel:$orbit")
        implementation("org.orbit-mvi:orbit-compose:$orbit")

        // Hilt
        implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationCompose")
        implementation("com.google.dagger:hilt-android:$hilt")
        kapt("com.google.dagger:hilt-compiler:$hilt")

        // Other
        implementation("androidx.activity:activity-compose:$activityCompose")
        implementation("androidx.navigation:navigation-compose:$navigationCompose")
        implementation("com.github.bhlangonijr:chesslib:$chessLib")
        implementation("com.jakewharton.timber:timber:$timber")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")
        implementation("androidx.datastore:datastore-preferences:$datastorePreferences")

        // Test
        testImplementation("junit:junit:$junit")
        testImplementation("app.cash.turbine:turbine:$turbine")
    }
}
