plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    with(Versions) {
        api("com.github.bhlangonijr:chesslib:$chessLib")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    }
}
