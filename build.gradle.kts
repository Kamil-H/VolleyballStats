apply(plugin = "io.gitlab.arturbosch.detekt")

allprojects {
    repositories.applyDefault()

    buildscript {
        repositories.applyDefault()
    }
}

buildscript {
    repositories.applyDefault()
    // Defining plugins here let me omit versions in "plugins" block in each build.gradle file
    dependencies {
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.sqldelight)
        classpath(libs.plugin.serialization)
        classpath(libs.plugin.ksp)
        classpath(libs.plugin.shadow)
        classpath(libs.plugin.kover)
        classpath(libs.plugin.detekt)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    config.setFrom(file("detekt-config.yml"))
    source = objects.fileCollection().from(
        "server/src/main/kotlin",
        "shared/datetime/src/commonMain/kotlin",
        "shared/domain/src/commonMain/kotlin",
        "shared/storage/src/commonMain/kotlin",
        "shared/network/src/commonMain/kotlin",
        "shared/api/src/commonMain/kotlin",
    ).asFileTree
    allRules = true
}