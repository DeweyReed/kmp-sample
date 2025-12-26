plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.feature.home.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.base.data)
                api(projects.feature.home.api)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.kotlinxJson)
            }
        }
    }
}
