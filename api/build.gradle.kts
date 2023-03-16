plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("kotlinx-serialization")
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
        implementation(project(":common"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
        implementation("com.github.bhlangonijr:chesslib:$chessLib")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")

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
