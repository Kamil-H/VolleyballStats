apply(plugin = "detekt-plugin")
apply(plugin = "dependency-graph")

allprojects {
    repositories.applyDefault()

    buildscript {
        repositories.applyDefault()
    }
}
