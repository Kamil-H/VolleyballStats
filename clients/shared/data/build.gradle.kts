plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:api"))
    commonMainImplementation(project(":shared:storage"))
    commonMainApi(project(":shared:network"))

    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonMainImplementation(libs.inject.runtime)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(project(":shared:domain:test"))
    commonTestImplementation(project(":shared:network:test"))
}