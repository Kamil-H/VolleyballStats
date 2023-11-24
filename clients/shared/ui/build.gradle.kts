plugins {
    `compose-library-plugin`
    id("org.jetbrains.compose")
    kotlin("native.cocoapods")
    id("com.google.devtools.ksp")
}

kotlin {
    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../../ios-compose/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
}

android {
    namespace = "com.kamilh.volleyballstats.ui"
}

dependencies {
    commonMainImplementation(project(":clients:shared:presentation"))
    commonMainImplementation(project(":shared:domain"))

    commonMainImplementation(libs.jetbrains.compose.ui)
    commonMainImplementation(libs.jetbrains.compose.material3)

    androidMainImplementation(libs.androidx.lifecycle.runtime)
    androidMainImplementation(libs.androidx.core)
    androidMainImplementation(libs.coil)

    kspIosArm64(libs.inject.compiler)
    kspIosSimulatorArm64(libs.inject.compiler)
}