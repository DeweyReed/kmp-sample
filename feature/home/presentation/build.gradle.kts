plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.metro)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.feature.home.presentation"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.presentation)
                implementation(projects.feature.home.api)
                implementation(projects.feature.home.data)
            }
        }
    }
}
