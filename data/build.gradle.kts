plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.data)
                implementation(projects.feature.home.data)
                implementation(libs.koin.core)
            }
        }
    }
}
