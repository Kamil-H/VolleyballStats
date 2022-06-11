allprojects {
    repositories.applyDefault()

    buildscript {
        repositories.applyDefault()
    }
}

buildscript {
    repositories.applyDefault()
    // Defining plugins here let me omit versions in "plugins" block in each build.gradle file
    dependencies {
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.sqldelight)
        classpath(libs.plugin.serialization)
        classpath(libs.plugin.ksp)
        classpath(libs.plugin.shadow)
    }
}
