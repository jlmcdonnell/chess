plugins {
    with(Versions) {
        id("androidx.baselineprofile") version baselineProfilePlugin apply false
        id("com.android.application") version androidGradlePlugin apply false
        id("com.android.test") version androidGradlePlugin apply false
        id("com.android.library") version androidGradlePlugin apply false
        id("com.google.dagger.hilt.android") version hilt apply false
        id("org.jetbrains.kotlin.android") version kotlin apply false
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin apply false
        id("org.jlleitschuh.gradle.ktlint") version ktlint apply false
    }
}

tasks.create("clean", Delete::class) {
    delete.add(rootProject.buildDir)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
