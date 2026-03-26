plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
}

dependencies {
    api(libs.chesslib)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
