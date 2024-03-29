enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "volleyball-stats"
include("server")
include("shared:datetime")
include("shared:domain")
include("shared:domain:test")
include("shared:storage")
include("shared:storage:test")
include("shared:network")
include("shared:network:test")
include("shared:api")
