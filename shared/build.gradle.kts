import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.convention.metro)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.shared"
    }
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.base.presentation)
            implementation(projects.feature.home.presentation)
            implementation(projects.feature.settings.presentation)
            implementation(projects.data)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewModelNav3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
