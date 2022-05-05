package com.kamilh

import com.kamilh.models.TestAppConfig
import com.kamilh.utils.testAppDispatchers
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class ApplicationTest(applicationTestBuilder: ApplicationTestBuilder) {

    private val databaseCalls: MutableList<Database.() -> Unit> = mutableListOf()
    private var applicationStarted: Boolean = false

    private val scope = CoroutineScope(testAppDispatchers.default)
    private val appModule = AppModule::class.create(scope = scope, appConfig = TestAppConfig())

    val testComponent = TestComponent::class.create(appModule)
    private val database = testComponent.databaseFactory.database

    val client: HttpClient = applicationTestBuilder.client

    init {
        applicationTestBuilder.environment {
            config = ApplicationConfig("application-test.conf")
        }
        applicationTestBuilder.application {
            module(appModule)
            start()
            environment.monitor.subscribe(ApplicationStopped) {
                stop()
            }
        }
    }

    fun withDatabase(block: Database.() -> Unit) {
        if (applicationStarted) {
            block(database)
        } else {
            databaseCalls.add(block)
        }
    }

    private fun start() {
        databaseCalls.forEach {
            it.invoke(database)
        }
        applicationStarted = true
    }

    private fun stop() {
        scope.cancel()
        applicationStarted = false
    }
}

fun applicationTest(block: suspend ApplicationTest.() -> Unit) = testApplication {
    val applicationTest = ApplicationTest(this)
    block(applicationTest)
}