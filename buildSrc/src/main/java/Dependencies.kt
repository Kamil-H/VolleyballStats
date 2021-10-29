object Constants {
    const val version = "0.0.1"
    const val packageName = "com.kamilh"
}

object Dependencies {
    const val kotlinVersion = "1.5.30"
    const val sqlDelightVersion = "1.5.1"

    object Plugins {
        const val jvm = "jvm"
        const val serialization = "plugin.serialization"
        const val sqlDelight = "com.squareup.sqldelight"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    }

    object Ktor {
        private const val version = "1.6.3"
        const val netty = "io.ktor:ktor-server-netty:$version"
        const val core = "io.ktor:ktor-server-core:$version"
        const val hostCommon = "io.ktor:ktor-server-host-common:$version"
        const val auth = "io.ktor:ktor-auth:$version"
        const val serialization = "io.ktor:ktor-serialization:$version"
        const val clientSerialization = "io.ktor:ktor-client-serialization:$version"
        const val json = "io.ktor:ktor-client-json:$version"
        const val jvm = "io.ktor:ktor-client-apache:$version"
        const val websockets = "io.ktor:ktor-client-websockets:$version"
        const val cio = "io.ktor:ktor-client-cio:$version"
        const val logging = "io.ktor:ktor-client-logging:$version"

        object Test {
            const val server = "io.ktor:ktor-server-tests:$version"
            const val client = "io.ktor:ktor-client-tests:$version"
        }
    }

    object Logback {
        private const val version = "1.2.1"
        const val classic = "ch.qos.logback:logback-classic:$version"
    }

    object Coroutines {
        private const val version = "1.5.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"

        object Test {
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object Kodein {
        private const val version = "7.5.0"
        const val server = "org.kodein.di:kodein-di-framework-ktor-server-jvm:$version"
    }

    object SqlDelight {
        private const val version = sqlDelightVersion
        const val driver = "com.squareup.sqldelight:sqlite-driver:$version"
        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
    }

    object Jsoup {
        private const val version = "1.13.1"
        const val jsoup = "org.jsoup:jsoup:$version"
    }

    object DateTime {
        private const val version = "0.1.1"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
    }

    object JUnit5 {
        private const val version = "5.7.0"
        const val jupiter = "org.junit.jupiter:junit-jupiter:$version"
        const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
        const val params = "org.junit.jupiter:junit-jupiter-params:$version"
    }
}