package com.kamilh.volleyballstats.routes

import com.kamilh.volleyballstats.AppModule
import com.kamilh.volleyballstats.TestComponent
import com.kamilh.volleyballstats.api.AccessToken
import com.kamilh.volleyballstats.api.StatsApi
import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.create
import com.kamilh.volleyballstats.models.appConfigOf
import com.kamilh.volleyballstats.module
import com.kamilh.volleyballstats.utils.testAppDispatchers
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
    private val accessToken = "application_test"
    private val appModule = AppModule::class.create(
        scope = scope,
        appConfig = appConfigOf(accessTokens = listOf(AccessToken(accessToken)))
    )
    private val testComponent = TestComponent::class.create(appModule)

    val client: HttpClient by lazy {
        applicationTestBuilder.createClient {
            install(ContentNegotiation) {
                json(json = testComponent.json)
            }
        }
    }
    val statsApi: StatsApi = testComponent.statsApi
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
        header(ApiConstants.HEADER_ACCESS_TOKEN, accessToken)
    }
}

fun testServerApplication(block: suspend ServerApplicationTestBuilder.() -> Unit) = testApplication {
    val applicationTest = ServerApplicationTestBuilder(this)
    block(applicationTest)
}