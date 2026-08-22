plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.base.data)
                api(projects.feature.home.data)

                implementation(libs.kotlinx.io.byteString)

                api(libs.room3.runtime)
                implementation(libs.androidx.sqlite.bundled)

                implementation(libs.ktor.core)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.kotlinxJson)

                implementation(libs.paths)
            }
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup)
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

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room3.compiler)
    add("kspIosArm64", libs.room3.compiler)
    add("kspIosSimulatorArm64", libs.room3.compiler)
    add("kspJvm", libs.room3.compiler)
}
