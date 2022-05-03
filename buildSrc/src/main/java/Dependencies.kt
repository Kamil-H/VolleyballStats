object Constants {
    const val version = "0.0.1"
    const val packageName = "com.kamilh"
}

object Dependencies {
    const val kotlinVersion = "1.6.21"
    const val sqlDelightVersion = "1.5.3"
    const val kspVersion = "1.6.21-1.0.5"

    object Plugins {
        const val jvm = "jvm"
        const val serialization = "plugin.serialization"
        const val sqlDelight = "com.squareup.sqldelight"
        const val ksp = "com.google.devtools.ksp"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    }

    object Ktor {
        private const val version = "2.0.1"

        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:$version"

        object Server {
            const val netty = "io.ktor:ktor-server-netty:$version"
            const val core = "io.ktor:ktor-server-core:$version"
            const val hostCommon = "io.ktor:ktor-server-host-common:$version"
            const val auth = "io.ktor:ktor-server-auth:$version"
            const val statusPages = "io.ktor:ktor-server-status-pages:$version"
            const val contentNegotiate = "io.ktor:ktor-server-content-negotiation:$version"

            const val test = "io.ktor:ktor-server-tests:$version"
        }

        object Client {
            const val contentNegotiate = "io.ktor:ktor-client-content-negotiation:$version"
            const val jvm = "io.ktor:ktor-client-apache:$version"
            const val websockets = "io.ktor:ktor-client-websockets:$version"
            const val cio = "io.ktor:ktor-client-cio:$version"
            const val logging = "io.ktor:ktor-client-logging:$version"

            const val test = "io.ktor:ktor-client-tests:$version"
        }
    }

    object Logback {
        private const val version = "1.2.1"
        const val classic = "ch.qos.logback:logback-classic:$version"
    }

    object Coroutines {
        private const val version = "1.6.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"

        object Test {
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object KotlinInject {
        private const val version = "0.4.1"
        const val runtime = "me.tatarka.inject:kotlin-inject-runtime:$version"
        const val compiler = "me.tatarka.inject:kotlin-inject-compiler-ksp:$version"
    }

    object SqlDelight {
        private const val version = sqlDelightVersion
        const val driver = "com.squareup.sqldelight:sqlite-driver:$version"
        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val coroutinesExtension = "com.squareup.sqldelight:coroutines-extensions-jvm:$version"
    }

    object Turbine {
        private const val version = "0.7.0"
        const val turbine = "app.cash.turbine:turbine:$version"
    }

    object SQLiteDriver {
        private const val version = "3.36.0.3"
        const val jdbc = "org.xerial:sqlite-jdbc:$version"
    }

    object Jsoup {
        private const val version = "1.13.1"
        const val jsoup = "org.jsoup:jsoup:$version"
    }

    object DateTime {
        private const val version = "0.6.3"
        const val dateTime = "io.islandtime:core:$version"
    }

    object JUnit5 {
        private const val version = "5.7.0"
        const val jupiter = "org.junit.jupiter:junit-jupiter:$version"
        const val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
        const val params = "org.junit.jupiter:junit-jupiter-params:$version"
    }
}