plugins {
    `kotlin-dsl`
}

group = "com.github.deweyreed.souvenir.buildlogic"

java {
    val javaVersion = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget
            .fromTarget(libs.versions.jvmTarget.get())
    }
}

dependencies {
    compileOnly(plugin(libs.plugins.kotlin.multiplatform))
    compileOnly(plugin(libs.plugins.android.application))
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
        register("kotlinMultiplatform") {
            id = libs.plugins.convention.kotlinMultiplatform.get().pluginId
            implementationClass = "KotlinMultiplatformConventionPlugin"
        }
    }
}
