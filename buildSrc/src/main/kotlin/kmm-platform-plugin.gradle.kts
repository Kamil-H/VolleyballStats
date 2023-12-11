plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlin-parcelize")
}

kotlin {
    targetsAndSourceSets {
        androidTarget()
        jvm("desktop")
    }
}

androidConfiguration()
