plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.metro)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.feature.home.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.data)
                implementation(projects.feature.home.api)

                implementation(libs.room3.runtime)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.mock)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.kotlinxJson)
            }
        }
    }
}
