plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.github.deweyreed.souvenir.base.data"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.base.api)

            implementation(libs.kotlinx.serialization.json)

            api(libs.room.runtime)

            api(libs.datastore.core)
            api(libs.datastore.preferences)

            api(libs.koin.core)

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
