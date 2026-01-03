import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.kmp.application)
    alias(libs.plugins.convention.kmp.compose)
    alias(libs.plugins.compose.hotReload)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.base.presentation)
            implementation(projects.feature.home.presentation)
            implementation(projects.data)
            implementation(libs.androidx.lifecycle.viewModelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

android {
    namespace = "com.github.deweyreed.souvenir"

    defaultConfig {
        applicationId = "com.github.deweyreed.souvenir"
        versionCode = libs.versions.version.code.get().toInt()
        versionName = libs.versions.version.name.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
