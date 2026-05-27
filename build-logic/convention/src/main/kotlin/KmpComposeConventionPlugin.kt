
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            confitureCompose()

            extensions.findByType(KotlinMultiplatformExtension::class)?.run {
                sourceSets.commonMain.dependencies {
                    implementation(libs.findLibrary("compose-foundation").get())
                    implementation(libs.findLibrary("compose-material3").get())
                    implementation(libs.findLibrary("compose-resources").get())
                }
            }
        }
    }
}
