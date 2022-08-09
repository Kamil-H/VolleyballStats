plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:domain"))
    commonMainImplementation(project(":shared:network"))
    commonMainImplementation(project(":shared:network:test"))
    commonMainImplementation(project(":clients:shared:data"))
}