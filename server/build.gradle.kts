plugins {
    application
    kotlin(Dependencies.Plugins.jvm) version Dependencies.kotlinVersion
    kotlin(Dependencies.Plugins.serialization) version Dependencies.kotlinVersion
    id(Dependencies.Plugins.sqlDelight) version Dependencies.sqlDelightVersion
    id(Dependencies.Plugins.ksp) version Dependencies.kspVersion
}

group = Constants.packageName
version = Constants.version

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

buildscript {
    dependencies {
        classpath(Dependencies.SqlDelight.plugin)
    }
}

sqldelight {
    database(name = "Database") {
        packageName = Constants.packageName
        dialect = "sqlite:3.25"
        deriveSchemaFromMigrations = true
    }
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Coroutines.core)

    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Ktor.Server.netty)
    implementation(Dependencies.Ktor.Server.core)
    implementation(Dependencies.Ktor.Server.hostCommon)
    implementation(Dependencies.Ktor.Server.auth)
    implementation(Dependencies.Ktor.Server.statusPages)
    implementation(Dependencies.Ktor.Server.contentNegotiate)

    implementation(Dependencies.Ktor.Client.contentNegotiate)
    implementation(Dependencies.Ktor.Client.jvm)
    implementation(Dependencies.Ktor.Client.websockets)
    implementation(Dependencies.Ktor.Client.cio)
    implementation(Dependencies.Ktor.Client.logging)

    implementation(Dependencies.Logback.classic)

    implementation(Dependencies.SqlDelight.driver)
    implementation(Dependencies.SqlDelight.coroutinesExtension)
    implementation(Dependencies.SQLiteDriver.jdbc)

    implementation(Dependencies.Turbine.turbine)

    implementation(Dependencies.Jsoup.jsoup)

    implementation(Dependencies.DateTime.dateTime)

    ksp(Dependencies.KotlinInject.compiler)
    implementation(Dependencies.KotlinInject.runtime)

    testImplementation(Dependencies.Coroutines.Test.test)
    testImplementation(Dependencies.Ktor.Server.test)
    testImplementation(Dependencies.Ktor.Client.test)
    testImplementation(Dependencies.JUnit5.jupiter)
    testImplementation(Dependencies.JUnit5.engine)
    testImplementation(Dependencies.JUnit5.params)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
    kotlinOptions.jvmTarget = "11"
}

tasks.register("prepareKotlinBuildScriptModel") { }