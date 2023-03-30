plugins {
    id("com.android.test")
    id("kotlin-android")
    id("androidx.baselineprofile")
}

android {
    namespace = "dev.mcd.chess.baselineprofile"
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        minSdk = BuildSettings.minSdk
        targetSdk = BuildSettings.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"


    baselineProfile {
        useConnectedDevices = false
    }
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
}

dependencies {
    with (Versions) {
        implementation("androidx.test.ext:junit:$junitExt")
        implementation("androidx.test.uiautomator:uiautomator:$uiautomator")
        implementation("androidx.benchmark:benchmark-macro-junit4:$androidBenchmarkJunit")
    }
}
