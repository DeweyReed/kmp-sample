
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("compose-multiplatform").get().get().pluginId)
            apply(plugin = libs.findPlugin("compose-compiler").get().get().pluginId)

            extensions.findByType(KotlinMultiplatformExtension::class)?.run {
                sourceSets.commonMain.dependencies {
                    implementation(libs.findLibrary("compose-foundation").get())
                    implementation(libs.findLibrary("compose-material3").get())
                    implementation(libs.findLibrary("compose-resources").get())
                }
            }

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                val isolated = this@with.isolated
                stabilityConfigurationFiles.addAll(
                    isolated.rootProject.projectDirectory.file("stability-config.conf")
                        .also { require(it.asFile.exists()) },
                )
            }
        }
    }
}
