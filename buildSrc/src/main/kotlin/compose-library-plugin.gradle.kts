plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
    targetsAndSourceSets {
        androidTarget()
        jvm("desktop")
    }
}

androidConfiguration()
