plugins {
    `compose-library-plugin`
    id("org.jetbrains.compose")
}

dependencies {
    commonMainImplementation(project(":clients:shared:presentation"))

    commonMainImplementation(libs.jetbrains.compose.ui)
    commonMainImplementation(libs.jetbrains.compose.material3)

    androidMainImplementation(libs.accompanist.flowlayout)
    androidMainImplementation(libs.androidx.lifecycle.runtime)
}