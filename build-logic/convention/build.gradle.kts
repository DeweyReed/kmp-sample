plugins {
    `kotlin-dsl`
}

group = "com.github.deweyreed.souvenir.buildlogic"

java {
    val javaVersion = JavaVersion.toVersion(libs.versions.buildLogicJvmTarget.get())
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget
            .fromTarget(libs.versions.buildLogicJvmTarget.get())
    }
}

dependencies {
    compileOnly(plugin(libs.plugins.kotlin.multiplatform))
    compileOnly(plugin(libs.plugins.compose.multiplatform))
    compileOnly(plugin(libs.plugins.compose.compiler))
    compileOnly(plugin(libs.plugins.android.kmpLibrary))
    compileOnly(plugin(libs.plugins.android.lint))
    compileOnly(plugin(libs.plugins.metro))
}

private fun plugin(plugin: Provider<PluginDependency>): Provider<String> {
    return plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register(libs.plugins.convention.kmp.library.get().pluginId) {
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register(libs.plugins.convention.kmp.compose.get().pluginId) {
            implementationClass = "KmpComposeConventionPlugin"
        }
        register(libs.plugins.convention.metro.get().pluginId) {
            implementationClass = "MetroConventionPlugin"
        }
    }
}
