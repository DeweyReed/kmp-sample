plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.metro)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.base.data"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.base.api)

            implementation(libs.kotlinx.serialization.json)

            api(libs.ktor.core)
            implementation(libs.ktor.contentNegotiation)
            implementation(libs.ktor.kotlinxJson)
        }
        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.java)
        }
    }
}
