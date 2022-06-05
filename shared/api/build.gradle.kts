plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization")
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:network"))
    commonMainImplementation(project(":shared:datetime"))

    commonMainImplementation(libs.inject.runtime)
    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.kotlinx.serialization)
}