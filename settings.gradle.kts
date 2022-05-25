enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "volleyball-stats"
include("server")
include("shared:datetime")
