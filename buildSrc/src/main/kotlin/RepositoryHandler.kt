import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun RepositoryHandler.applyDefault() {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven { url = URI("https://oss.sonatype.org/content/repositories/snapshots") }
}