plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.feature.home.api"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.base.api)
            }
        }
    }
}
