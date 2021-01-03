plugins {
    application
    kotlin(Dependencies.Plugins.jvm) version Dependencies.kotlinVersion
    kotlin(Dependencies.Plugins.serialization) version Dependencies.kotlinVersion
}

group = Constants.packageName
version = Constants.version

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Coroutines.core)

    implementation(Dependencies.Ktor.netty)
    implementation(Dependencies.Ktor.core)
    implementation(Dependencies.Ktor.hostCommon)
    implementation(Dependencies.Ktor.auth)
    implementation(Dependencies.Ktor.serialization)

    implementation(Dependencies.Kodein.server)

    implementation(Dependencies.Logback.classic)

    testImplementation(Dependencies.Coroutines.Test.test)
    testImplementation(Dependencies.Ktor.Test.test)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.jvmTarget = "1.8"
}
