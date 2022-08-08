plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:domain:test"))
    commonMainImplementation(project(":shared:interactors"))
    commonMainImplementation(libs.kotlinx.coroutines.core)
}