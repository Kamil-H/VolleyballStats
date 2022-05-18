plugins {
    kotlin(Dependencies.Plugins.multiplatform) version Dependencies.kotlinVersion
}

kotlin {
    jvm()
    ios()

    sourceSets {
        val commonMain by getting
    }
}

dependencies {
    commonMainImplementation(Dependencies.DateTime.dateTime)
}