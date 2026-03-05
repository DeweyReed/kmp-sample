plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.feature.home.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.data)
                implementation(projects.feature.home.api)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.ktor.mock)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.kotlinxJson)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}
