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
                implementation(projects.base.data)
                implementation(projects.feature.home.api)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.koin.core)
            }
        }
    }
}
