plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.metro)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                // Classes in data modules need to be visible to metro
                api(projects.base.data)
                api(projects.feature.home.data)
            }
        }
    }
}
