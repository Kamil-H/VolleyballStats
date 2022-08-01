import util.libs

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    ios()

    sourceSets {
        val commonMain by getting

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}