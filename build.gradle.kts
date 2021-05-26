plugins {
    application
    kotlin(Dependencies.Plugins.jvm) version Dependencies.kotlinVersion
    kotlin(Dependencies.Plugins.serialization) version Dependencies.kotlinVersion
    id(Dependencies.Plugins.sqlDelight) version Dependencies.sqlDelightVersion
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

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.SqlDelight.plugin)
    }
}

sqldelight {
    database(name = "Database") {
        packageName = Constants.packageName
    }
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Coroutines.core)

    implementation(Dependencies.Ktor.netty)
    implementation(Dependencies.Ktor.core)
    implementation(Dependencies.Ktor.hostCommon)
    implementation(Dependencies.Ktor.auth)
    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Ktor.clientSerialization)
    implementation(Dependencies.Ktor.json)
    implementation(Dependencies.Ktor.jvm)
    implementation(Dependencies.Ktor.websockets)
    implementation(Dependencies.Ktor.cio)
    implementation(Dependencies.Ktor.logging)

    implementation(Dependencies.Kodein.server)

    implementation(Dependencies.Logback.classic)

    implementation(Dependencies.SqlDelight.driver)

    implementation(Dependencies.Jsoup.jsoup)

    testImplementation(Dependencies.Coroutines.Test.test)
    testImplementation(Dependencies.Ktor.Test.server)
    testImplementation(Dependencies.Ktor.Test.client)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.jvmTarget = "1.8"
}
