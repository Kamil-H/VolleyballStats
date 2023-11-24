import gradle.kotlin.dsl.accessors._7767e1ac0d2e2038733d8ae804e51b51.android
import gradle.kotlin.dsl.accessors._7767e1ac0d2e2038733d8ae804e51b51.sourceSets
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import util.libs

fun KotlinMultiplatformExtension.targetsAndSourceSets(additionalTargets: KotlinMultiplatformExtension.() -> Unit) {
    additionalTargets()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

fun Project.androidConfiguration() {
    android {
        compileSdk = libs.versions.android.compile.get().toInt()

        defaultConfig {
            minSdk = libs.versions.android.min.get().toInt()
            targetSdk = libs.versions.android.target.get().toInt()
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildTypes {
            register("local") {
                initWith(getByName("debug"))
            }
        }
    }
}
