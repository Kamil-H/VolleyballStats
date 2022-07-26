plugins {
    id("org.gradle.application")
    id("com.google.devtools.ksp")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("kover")
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":shared:domain"))
    implementation(project(":shared:datetime"))
    implementation(project(":shared:storage"))
    implementation(project(":shared:network"))
    implementation(project(":shared:api"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.hostCommon)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.contentNegotiate)
    implementation(libs.ktor.server.callLogging)

    implementation(libs.ktor.client.contentNegotiate)
    implementation(libs.ktor.client.jvm)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)

    implementation(libs.logback.classic)

    implementation(libs.sqldelight.driver.jvm)
    implementation(libs.sqlite.jdbc)

    implementation(libs.turbine)

    implementation(libs.jsoup)

    ksp(libs.inject.compiler)
    implementation(libs.inject.runtime)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.server.test)
    testImplementation(libs.ktor.client.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.engine)
    testImplementation(libs.junit.params)
    testImplementation(project(":shared:domain:test"))
    testImplementation(project(":shared:storage:test"))
    testImplementation(project(":shared:network:test"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.jvmTarget = "11"
}

tasks.register("prepareKotlinBuildScriptModel") { }