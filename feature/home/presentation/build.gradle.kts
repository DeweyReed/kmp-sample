plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.kmp.compose)
}

kotlin {
    androidLibrary {
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
                // implementation(libs.koin.test)
                // implementation(libs.ktor.mock)
                // implementation(libs.ktor.contentNegotiation)
                // implementation(libs.ktor.kotlinxJson)
            }
        }
    }
}
