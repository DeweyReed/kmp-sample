plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.base.data"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.base.api)

            api(libs.room.runtime)

            api(libs.datastore.core)
            api(libs.datastore.preferences)

            api(libs.ktor.core)
        }
    }
}
