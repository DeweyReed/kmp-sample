plugins {
    alias(libs.plugins.convention.kmp.application)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.shared"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.base.presentation)
            implementation(projects.feature.home.presentation)
            implementation(projects.feature.settings.presentation)
            implementation(projects.data)

            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
