plugins {
    id("com.android.library")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "dev.mcd.chess.lc0"
    compileSdk = BuildSettings.compileSdk

    defaultConfig {
        minSdk = BuildSettings.minSdk

        ndk {
            abiFilters.clear()
            abiFilters += "arm64-v8a"
        }
    }

    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
    }

    kotlin {
        jvmToolchain(BuildSettings.jdkVersion)
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/native/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

val lczeroSrc = "src/main/native/lczero"
val unzipIncludes = project.tasks.register<DefaultTask>("unzipIncludes") {
    val destDir = file(lczeroSrc)
    outputs.upToDateWhen { destDir.exists() }
    doLast {
        project.copy {
            from(zipTree(file("$lczeroSrc.zip")))
            into(destDir)
        }
    }
}

tasks.named("clean").configure {
    doLast {
        project.delete(lczeroSrc)
    }
}

tasks.named("preBuild").configure {
    dependsOn(unzipIncludes)
}

dependencies {
    api(project(":common"))
    api(libs.jakewharton.timber)
    api(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)
}
