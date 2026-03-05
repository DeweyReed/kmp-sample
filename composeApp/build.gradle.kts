import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.kmp.application)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.github.deweyreed.souvenir.app"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.base.presentation)
            implementation(projects.feature.home.presentation)
            implementation(projects.data)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.compose.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.deweyreed.souvenir.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.deweyreed.souvenir"
            packageVersion = libs.versions.version.name.get()
        }
    }
}
