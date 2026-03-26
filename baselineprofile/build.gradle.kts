plugins {
    id("com.android.test")
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
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.test.runner)
}
