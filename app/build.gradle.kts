plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.compose)
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

        buildConfigField(
            type = "String",
            name = "ONLINE_API_HOST",
            value = "\"${AppConfig.ONLINE_API_HOST}\"",
        )
    }

    buildFeatures {
        aidl = true
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

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalStdlibApi")
        freeCompilerArgs.add("-Xcontext-parameters")
        if (project.hasProperty("enableComposeCompilerReports")) {
            val metricsDir = project.layout.buildDirectory.dir("compose_metrics").get().asFile.absolutePath
            freeCompilerArgs.addAll(
                listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsDir",
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$metricsDir",
                ),
            )
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    // Projects
    implementation(project(":engine-stockfish"))
    implementation(project(":engine-lc0"))
    implementation(project(":common"))
    implementation(project(":online"))
    "baselineProfile"(project(mapOf("path" to ":baselineprofile")))

    // Core
    implementation(libs.slf4j.nop)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.profileinstaller)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Orbit
    implementation(libs.orbit.core)
    implementation(libs.orbit.viewmodel)
    implementation(libs.orbit.compose)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)

    // Other
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.chesslib)
    implementation(libs.jakewharton.timber)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.benchmark.macro.junit4)
}
