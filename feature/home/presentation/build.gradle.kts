plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.feature.home.presentation"
        androidResources.enable = true
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.presentation)
                implementation(projects.feature.home.api)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
