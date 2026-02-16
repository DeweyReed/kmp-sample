
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureKmpLibrary()
    }
}
