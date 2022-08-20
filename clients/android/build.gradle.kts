plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        applicationId = libs.plugins.application.`package`.get().pluginId
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = libs.versions.android.versionCode.get().toInt()
        versionName = libs.versions.application.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
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

    ksp(libs.inject.compiler)
    implementation(libs.inject.runtime)
    implementation(libs.sqldelight.driver.android)

    implementation(libs.ktor.client.contentNegotiate)
    implementation(libs.ktor.client.jvm)
    implementation(libs.ktor.client.logging)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
}