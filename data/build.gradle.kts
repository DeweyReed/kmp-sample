plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.data"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.base.data)
                implementation(projects.feature.home.data)

                implementation(libs.room.runtime)
                implementation(libs.room.sqliteBundled)

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

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}
