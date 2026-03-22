plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.base.api"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
