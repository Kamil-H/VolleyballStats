repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    // All of those plugin will be available in project's classpath
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.android)
    implementation(libs.plugin.sqldelight)
    implementation(libs.plugin.serialization)
    implementation(libs.plugin.ksp)
    implementation(libs.plugin.shadow)
    implementation(libs.plugin.kover)
    implementation(libs.plugin.detekt)
    implementation(libs.plugin.jetbrains.compose)
    implementation(libs.plugin.libres)
    implementation(libs.plugin.dependency.graph)
}