plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    android {
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
