plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.inject.runtime)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(libs.test.annotations.common)
    commonTestImplementation(libs.ktor.server.test)
    commonTestImplementation(libs.ktor.client.test)
    commonTestImplementation(project(":shared:network:test"))
}