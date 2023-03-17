plugins {
    id("org.jetbrains.kotlin.jvm")
    id("kotlinx-serialization")
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

        // Ktor
        api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        api("io.ktor:ktor-client-core:$ktor")
        api("io.ktor:ktor-client-okhttp:$ktor")
        api("io.ktor:ktor-client-content-negotiation:$ktor")
        api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
        api("io.ktor:ktor-client-logging:$ktor")

        testImplementation("io.ktor:ktor-client-cio:$ktor")
        testImplementation("junit:junit:$junit")
    }
}
