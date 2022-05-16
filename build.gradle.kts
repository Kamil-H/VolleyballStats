group = Constants.packageName
version = Constants.version

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    buildscript {
        repositories {
            google()
            mavenCentral()
        }
    }
}
