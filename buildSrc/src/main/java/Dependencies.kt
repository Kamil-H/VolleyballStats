object Constants {
    const val version = "0.0.1"
    const val packageName = "com.kamilh"
}

object Dependencies {
    const val kotlinVersion = "1.4.20"

    object Plugins {
        const val jvm = "jvm"
        const val serialization = "plugin.serialization"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    }

    object Ktor {
        private const val version = "1.4.3"
        const val netty = "io.ktor:ktor-server-netty:$version"
        const val core = "io.ktor:ktor-server-core:$version"
        const val hostCommon = "io.ktor:ktor-server-host-common:$version"
        const val auth = "io.ktor:ktor-auth:$version"
        const val serialization = "io.ktor:ktor-serialization:$version"

        object Test {
            const val test = "io.ktor:ktor-server-tests:$version"
        }
    }

    object Logback {
        private const val version = "1.2.1"
        const val classic = "ch.qos.logback:logback-classic:$version"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"

        object Test {
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object Kodein {
        private const val version = "7.1.0"
        const val server = "org.kodein.di:kodein-di-framework-ktor-server-jvm:$version"
    }
}