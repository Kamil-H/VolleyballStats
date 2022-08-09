plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:interactors"))
    commonMainImplementation(project(":shared:storage"))
    commonMainImplementation(project(":clients:shared:data"))

    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(libs.inject.runtime)

    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.test.common)
    commonTestImplementation(project(":shared:domain:test"))
    commonTestImplementation(project(":shared:storage:test"))
    commonTestImplementation(project(":shared:network:test"))
    commonTestImplementation(project(":clients:shared:data:test"))
}