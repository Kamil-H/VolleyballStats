plugins {
    `kmm-platform-plugin`
}

android {
    namespace = "com.kamilh.volleyballstats.presentation"
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:interactors"))
    commonMainImplementation(project(":shared:storage"))
    commonMainImplementation(project(":shared:api"))
    commonMainImplementation(project(":clients:shared:data"))
    commonMainImplementation(project(":clients:shared:storage"))

    commonMainImplementation(libs.kotlinx.serialization)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(libs.inject.runtime)

    androidMainImplementation(libs.sqldelight.driver.android)
    iosMainImplementation(libs.sqldelight.driver.ios)

    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.ktor.serialization)
    commonMainImplementation(libs.ktor.client.contentNegotiate)
    commonMainImplementation(libs.ktor.client.logging)
    androidMainImplementation(libs.ktor.client.okhttp)
    iosMainImplementation(libs.ktor.client.darwin)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(project(":shared:domain:test"))
    commonTestImplementation(project(":shared:storage:test"))
    commonTestImplementation(project(":shared:network:test"))
    commonTestImplementation(project(":shared:interactors:test"))
    commonTestImplementation(project(":clients:shared:data:test"))
}