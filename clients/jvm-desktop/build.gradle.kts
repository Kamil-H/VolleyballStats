import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

// to run the desktop app: "./gradlew :clients:jvm-desktop:run"
compose.desktop {
    application {
        mainClass = "com.kamilh.volleyballstats.clients.jvmdesktop.MainKt"
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "desktop"
            packageVersion = "1.0.0"
        }
    }
}


dependencies {
    implementation(project(":shared:domain"))
    implementation(project(":shared:datetime"))
    implementation(project(":shared:storage"))
    implementation(project(":shared:network"))
    implementation(project(":shared:api"))
    implementation(project(":shared:interactors"))
    implementation(project(":clients:shared:presentation"))
    implementation(project(":clients:shared:data"))
    implementation(project(":clients:shared:ui"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    ksp(libs.inject.compiler)
    implementation(libs.inject.runtime)

    implementation(libs.sqldelight.driver.jvm)
    implementation(libs.ktor.client.jvm)

    implementation(libs.appyx.navigation)
    implementation(libs.appyx.backstack)
    implementation(libs.appyx.spotlight)

    implementation(libs.jetbrains.compose.ui)
    implementation(libs.jetbrains.compose.material3)
    implementation(compose.desktop.currentOs)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}