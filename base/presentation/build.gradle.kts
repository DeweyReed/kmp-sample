plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.kmp.compose)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.base.presentation"
        androidResources.enable = true
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.base.api)

                api(libs.androidx.lifecycle.viewModelCompose)
                api(libs.androidx.lifecycle.runtimeCompose)

                api(libs.metro.viewModelCompose)

                api(libs.coil.core)
                implementation(libs.coil.ktor)

                api(libs.navigation)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
