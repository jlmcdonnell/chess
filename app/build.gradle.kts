@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
    id("androidx.baselineprofile")
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
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        val release = getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard/proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard/benchmark-rules.pro")
            signingConfig = release.signingConfig
        }
    }

    kotlin {
        jvmToolchain(BuildSettings.jdkVersion)
    }

    // Opt in to kotlin context receivers
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    with(Versions) {
        // Projects
        implementation(project(":enginestockfish"))
        implementation(project(":common"))
        implementation(project(":online"))
        "baselineProfile"(project(mapOf("path" to ":baselineprofile")))

        // Core
        implementation("org.slf4j:slf4j-nop:$slf4j")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
        implementation("androidx.core:core-ktx:$coreKtx")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleRuntimeKtx")
        implementation("androidx.profileinstaller:profileinstaller:$androidProfileInstaller")

        // Compose
        implementation(platform("androidx.compose:compose-bom:$compose"))
        androidTestImplementation(platform("androidx.compose:compose-bom:$compose"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.foundation:foundation")
        implementation("androidx.compose.material:material")
        implementation("androidx.compose.material:material-icons-extended")
        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-tooling-preview")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-test-manifest")

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
        implementation("androidx.datastore:datastore-preferences:$datastorePreferences")

        // Test
        testImplementation("junit:junit:$junit")
        testImplementation("app.cash.turbine:turbine:$turbine")
        androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")
        androidTestImplementation("androidx.benchmark:benchmark-macro-junit4:$androidBenchmarkJunit")
    }
}
