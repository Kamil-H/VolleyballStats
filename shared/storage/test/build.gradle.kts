plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:storage"))
    commonMainImplementation(libs.kotlinx.coroutines.core)
}