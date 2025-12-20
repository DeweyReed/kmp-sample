plugins {
    alias(libs.plugins.convention.kmp.library)
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
            }
        }
    }
}
