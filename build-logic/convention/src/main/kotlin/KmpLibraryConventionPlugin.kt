import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(
            plugin = target.libs.findPlugin("kotlin-multiplatform").get().get().pluginId
        )
        target.apply(
            plugin = target.libs.findPlugin("android-kmpLibrary").get().get().pluginId
        )

        target.extensions.configure<KotlinMultiplatformExtension> {
            configure<KotlinMultiplatformAndroidLibraryTarget> {
                compileSdk {
                    version = release(
                        target.libs.findVersion("android-compileSdk")
                            .get().requiredVersion.toInt()
                    )
                }
                minSdk =
                    target.libs.findVersion("android-minSdk").get().requiredVersion.toInt()
            }
            iosArm64()
            iosSimulatorArm64()
            jvm()
        }
    }
}
