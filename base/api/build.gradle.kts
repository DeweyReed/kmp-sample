plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.base.api"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}
