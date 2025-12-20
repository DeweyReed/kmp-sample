plugins {
    alias(libs.plugins.convention.kmp.library)
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
            }
        }
    }
}
