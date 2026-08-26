import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.appJvmTarget.get()))
        }
    }
    dependencies {
        implementation(projects.shared)
        implementation(libs.androidx.activity.compose)
        lintChecks(libs.compose.lintChecks)
    }
}

android {
    namespace = "com.github.deweyreed.souvenir"
    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "com.github.deweyreed.souvenir"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.version.code.get().toInt()
        versionName = libs.versions.version.name.get()
    }
    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.appJvmTarget.get().toInt())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            optimization {
                enable = true
            }
        }
    }
}
