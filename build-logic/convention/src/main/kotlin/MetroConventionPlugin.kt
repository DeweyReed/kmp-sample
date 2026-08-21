import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class MetroConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(plugin = target.libs.findPlugin("metro").get().get().pluginId)
        target.extensions.configure<MetroPluginExtension> {
            generateContributionProviders.set(true)
        }
    }
}
