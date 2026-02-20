import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.configureKmpLibrary(iosFrameworkBaseName: String? = null) {
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
        val iosTargets = listOf(
            iosArm64(),
            iosSimulatorArm64(),
        )
        if (iosFrameworkBaseName != null) {
            iosTargets.forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = iosFrameworkBaseName
                    isStatic = true
                }
            }
        }
        jvm()
    }
}
