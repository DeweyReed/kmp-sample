plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
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
            }
        }
    }
}
