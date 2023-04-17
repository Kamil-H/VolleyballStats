plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kamilh.volleyballstats.clients.app"

    buildFeatures {
        buildConfig = true
    }

    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        applicationId = libs.plugins.application.`package`.get().pluginId
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = libs.versions.android.versionCode.get().toInt()
        versionName = libs.versions.application.get()
    }

    buildTypes {
        signingConfigs.create("customDebug") {
            storeFile(file("keystores/debug.keystore"))
            storePassword("android")
            keyAlias("androiddebugkey")
            keyPassword("android")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            signingConfig = signingConfigs.getByName("customDebug")
        }
        register("local") {
            initWith(getByName("debug"))
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    this.sourceSets {
        get("debug").java {
            srcDir("build/generated/ksp/debug/kotlin")
        }
        get("release").java {
            srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

dependencies {
    implementation(project(":shared:domain"))
    implementation(project(":shared:datetime"))
    implementation(project(":shared:storage"))
    implementation(project(":shared:network"))
    implementation(project(":shared:api"))
    implementation(project(":shared:interactors"))
    implementation(project(":clients:shared:presentation"))
    implementation(project(":clients:shared:data"))
    implementation(project(":clients:shared:ui"))

    ksp(libs.inject.compiler)
    implementation(libs.inject.runtime)
    implementation(libs.sqldelight.driver.android)

    implementation(libs.ktor.client.contentNegotiate)
    implementation(libs.ktor.client.jvm)
    implementation(libs.ktor.client.logging)

    implementation(libs.appyx.core)

    coreLibraryDesugaring(libs.android.desugaring)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.jetbrains.compose.ui)
    implementation(libs.jetbrains.compose.material3)
    debugImplementation(libs.jetbrains.compose.ui.tooling)
}