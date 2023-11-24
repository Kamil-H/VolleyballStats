plugins {
    `kmm-domain-plugin`
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create(name = "Database") {
            packageName.set("com.kamilh.volleyballstats.storage")
            dialect(libs.sqldelight.dialect)
            deriveSchemaFromMigrations.set(true)
        }
    }
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))

    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonMainImplementation(libs.sqldelight.runtime)
    commonMainImplementation(libs.sqldelight.coroutines)
    commonMainImplementation(libs.sqldelight.adapters)

    commonMainImplementation(libs.inject.runtime)

    jvmTestImplementation(libs.sqldelight.driver.jvm)
    iosArm64TestImplementation(libs.sqldelight.driver.ios)
    iosSimulatorArm64TestImplementation(libs.sqldelight.driver.ios)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(libs.test.annotations.common)
    commonTestImplementation(libs.turbine)
    commonTestImplementation(project(":shared:domain:test"))
}