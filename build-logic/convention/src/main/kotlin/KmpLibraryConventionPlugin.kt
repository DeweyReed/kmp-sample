import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
            apply(plugin = libs.findPlugin("android-kmpLibrary").get().get().pluginId)

            extensions.configure<KotlinMultiplatformExtension> {
                androidLibrary {
                    compileSdk {
                        version = release(
                            libs.findVersion("android-compileSdk")
                                .get().requiredVersion.toInt()
                        )
                    }
                    minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                }
                iosArm64()
                iosSimulatorArm64()
                jvm()
            }
        }
    }
}
