package com.kamilh.routes

import com.kamilh.AppModule
import com.kamilh.TestComponent
import com.kamilh.create
import com.kamilh.models.TEST_ACCESS_TOKEN
import com.kamilh.models.TestAppConfig
import com.kamilh.module
import com.kamilh.utils.testAppDispatchers
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class ServerApplicationTestBuilder(applicationTestBuilder: ApplicationTestBuilder) {

    private val databaseCalls: MutableList<suspend TestComponent.Storages.() -> Unit> = mutableListOf()
    private var applicationStarted: Boolean = false

    private val scope = CoroutineScope(testAppDispatchers.default)
    private val appModule = AppModule::class.create(scope = scope, appConfig = TestAppConfig())
    private val testComponent = TestComponent::class.create(appModule)

    val client: HttpClient by lazy {
        applicationTestBuilder.createClient {
            install(ContentNegotiation) {
                json(json = testComponent.json)
            }
        }
    }
    val api: Api = testComponent.api
    val mappers: TestComponent.Mappers = testComponent.mappers

    init {
        applicationTestBuilder.environment {
            config = ApplicationConfig("application-test.conf")
        }
        applicationTestBuilder.application {
            module(inTest = true, appModule = appModule)
            start()
            environment.monitor.subscribe(ApplicationStopped) {
                stop()
            }
        }
    }

    suspend fun withStorages(block: suspend TestComponent.Storages.() -> Unit) {
        if (applicationStarted) {
            block(testComponent.storages)
        } else {
            databaseCalls.add(block)
        }
    }

    private fun start() {
        runBlocking {
            databaseCalls.forEach {
                it.invoke(testComponent.storages)
            }
        }
        applicationStarted = true
    }

    private fun stop() {
        scope.cancel()
        applicationStarted = false
    }

    fun HttpRequestBuilder.authorize(): HttpRequestBuilder = apply {
        header("StatsApi-Access-Token", TEST_ACCESS_TOKEN.value)
    }
}

fun testServerApplication(block: suspend ServerApplicationTestBuilder.() -> Unit) = testApplication {
    val applicationTest = ServerApplicationTestBuilder(this)
    block(applicationTest)
}