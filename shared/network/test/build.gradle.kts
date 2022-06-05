plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:network"))
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(libs.ktor.client.core)
}