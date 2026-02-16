import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureKmpLibrary(iosFrameworkBaseName = "ComposeApp")
    }
}
