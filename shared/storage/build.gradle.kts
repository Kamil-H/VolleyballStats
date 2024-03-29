plugins {
    `kmm-domain-plugin`
    id("com.squareup.sqldelight")
}

sqldelight {
    database(name = "Database") {
        packageName = "com.kamilh.volleyballstats.storage"
        dialect = "sqlite:3.25"
        deriveSchemaFromMigrations = true
    }
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))

    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonMainImplementation(libs.sqldelight.runtime)
    commonMainImplementation(libs.sqldelight.coroutines)

    commonMainImplementation(libs.inject.runtime)

    jvmTestImplementation(libs.sqldelight.driver.jvm)
    iosTestImplementation(libs.sqldelight.driver.ios)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(libs.test.annotations.common)
    commonTestImplementation(libs.turbine)
    commonTestImplementation(project(":shared:domain:test"))
}