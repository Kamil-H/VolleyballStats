plugins {
    `kmm-domain-plugin`
//    id("app.cash.sqldelight")
}

//sqldelight {
//    database(name = "Database") {
//        packageName = "com.kamilh.volleyballstats.clients.storage"
//        dependency(project(":shared:storage"))
//        deriveSchemaFromMigrations = true
//    }
//}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:storage"))

    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonMainImplementation(libs.sqldelight.runtime)
    commonMainImplementation(libs.sqldelight.coroutines)

    commonMainImplementation(libs.inject.runtime)

    jvmTestImplementation(libs.sqldelight.driver.jvm)
    iosArm64MainImplementation(libs.sqldelight.driver.ios)
    iosSimulatorArm64MainImplementation(libs.sqldelight.driver.ios)
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(libs.test.annotations.common)
    commonTestImplementation(libs.turbine)
    commonTestImplementation(project(":shared:domain:test"))
}
