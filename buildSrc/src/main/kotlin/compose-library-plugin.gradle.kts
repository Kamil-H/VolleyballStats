import util.libs

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        register("local") {
            initWith(getByName("debug"))
        }
    }
}