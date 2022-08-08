plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:network"))
    commonMainImplementation(project(":shared:storage"))
    commonMainImplementation(libs.inject.runtime)
    commonMainImplementation(libs.kotlinx.coroutines.core)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(project(":shared:storage:test"))
    commonTestImplementation(project(":shared:domain:test"))
    commonTestImplementation(project(":shared:network:test"))
}