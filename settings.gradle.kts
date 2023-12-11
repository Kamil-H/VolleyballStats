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
include("shared:interactors")
include("shared:interactors:test")
include("clients:shared:data")
include("clients:shared:data:test")
include("clients:shared:presentation")
include("clients:android")
include("clients:shared:storage")
include("clients:shared:ui")
include("clients:jvm-desktop")