val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val serialization_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.20"
}

group = "com.kamilh"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.jvmTarget = "1.8"
}
