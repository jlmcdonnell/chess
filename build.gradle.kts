import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ktlint) apply false
}

tasks.create("clean", Delete::class) {
    delete.add(rootProject.buildDir)
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<KtlintExtension> {
        version.set(rootProject.libs.versions.ktlintTool.get())
    }
}
