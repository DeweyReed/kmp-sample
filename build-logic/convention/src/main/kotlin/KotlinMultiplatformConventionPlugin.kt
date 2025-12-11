import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
            apply(plugin = libs.findPlugin("android-application").get().get().pluginId)

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget {
                    compilerOptions {
                        jvmTarget.set(
                            JvmTarget.fromTarget(
                                libs.findVersion("jvmTarget").get().requiredVersion
                            )
                        )
                    }
                }

                listOf(
                    iosArm64(),
                    iosSimulatorArm64()
                ).forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                    }
                }

                jvm()
            }

            extensions.configure<ApplicationExtension> {
                compileSdk {
                    version = release(
                        libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
                    )
                }
                defaultConfig {
                    minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                    targetSdk =
                        libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
                }
                compileOptions {
                    val javaVersion = JavaVersion.toVersion(
                        libs.findVersion("jvmTarget").get().requiredVersion
                    )
                    sourceCompatibility = javaVersion
                    targetCompatibility = javaVersion
                }
            }
        }
    }
}
