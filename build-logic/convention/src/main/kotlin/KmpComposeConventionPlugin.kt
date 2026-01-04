import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("compose-compiler").get().get().pluginId)
            apply(plugin = libs.findPlugin("compose-multiplatform").get().get().pluginId)

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    val compose =
                        extensions.getByName("compose")
                            as org.jetbrains.compose.ComposePlugin.Dependencies
                    implementation(compose.material3)
                    implementation(compose.components.resources)
                    implementation(compose.components.uiToolingPreview)
                }
            }
            extensions.configure<ComposeCompilerGradlePluginExtension> {
                val isolated = isolated
                stabilityConfigurationFiles.addAll(
                    isolated.rootProject.projectDirectory.file("stability-config.conf")
                        .also { require(it.asFile.exists()) },
                )
            }
        }
    }
}
