import util.libs

plugins {
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    detektPlugins(libs.plugin.detekt.compose.rules)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    config.setFrom(file("detekt-config.yml"))
    source = objects.fileCollection().from(
        "server/src/main/kotlin",
        "shared/datetime/src/commonMain/kotlin",
        "shared/domain/src/commonMain/kotlin",
        "shared/storage/src/commonMain/kotlin",
        "shared/network/src/commonMain/kotlin",
        "shared/api/src/commonMain/kotlin",
        "shared/interactors/src/commonMain/kotlin",
        "clients/shared/data/src/commonMain/kotlin",
        "clients/shared/presentation/src/commonMain/kotlin",
        "clients/shared/presentation/src/iosMain/kotlin",
        "clients/shared/presentation/src/androidMain/kotlin",
        "clients/shared/presentation/src/androidMain/kotlin",
        "clients/shared/presentation/src/desktopMain/kotlin",
        "clients/shared/ui/src/commonMain/kotlin",
        "clients/shared/ui/src/androidMain/kotlin",
        "clients/shared/ui/src/desktopMain/kotlin",
        "clients/shared/ui/src/iosMain/kotlin",
        "clients/android/src/main/java",
        "clients/jvm-desktop/src/main/kotlin",
    ).asFileTree
    allRules = true
}