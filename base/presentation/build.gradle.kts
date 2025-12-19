plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.base.presentation"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.base.api)
                api(libs.coil.core)
                implementation(libs.coil.ktor)
            }
        }
    }
}
