plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:datetime"))
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(libs.kotlinx.coroutines.core)
}