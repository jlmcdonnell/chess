plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(BuildSettings.jdkVersion)
}

dependencies {
    with(Versions) {
        api("com.github.bhlangonijr:chesslib:$chessLib")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    }
}
