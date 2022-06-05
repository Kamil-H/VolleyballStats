plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.inject.runtime)
}