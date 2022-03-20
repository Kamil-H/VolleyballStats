package com.kamilh

import com.kamilh.authorization.CredentialsValidator
import com.kamilh.authorization.SubscriptionKey
import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.Result
import com.kamilh.models.TestAppConfig
import com.kamilh.models.api.user.UserResponse
import com.kamilh.routes.user.UserController
import com.kamilh.storage.common.adapters.*
import kotlinx.serialization.json.Json
import org.kodein.di.*
import routes.CallResult
import storage.AppConfigDatabaseFactory
import storage.DatabaseFactory
import storage.common.adapters.ZonedDateTimeAdapter

private const val MODULE_NAME = "DI_TEST_APPLICATION_MODULE"
fun testApplicationModule(
    userController: UserController = userControllerOf(),
    credentialsValidator: CredentialsValidator = credentialsValidatorOf(),
) = DI.Module(name = MODULE_NAME) {

    bindProvider { TestAppConfig() }
    bind<Json>() with provider { Json }
    bind<DatabaseFactory>() with provider { AppConfigDatabaseFactory(instance(), UuidAdapter(), ZonedDateTimeAdapter(),
        UrlAdapter(), TeamIdAdapter(), PlayerIdAdapter(), CountryAdapter(), LocalDateAdapter(), LocalDateTimeAdapter(),
        TourYearAdapter(), SpecializationAdapter(), MatchReportIdAdapter(), DurationAdapter(), PositionAdapter(),
        MatchIdAdapter(), TourIdAdapter())
    }
    bind<UserController>() with provider { userController }
    bind<CredentialsValidator>() with provider { credentialsValidator }
}

fun userResponseOf(
    id: Long = 0,
    subscriptionKey: String = "",
    deviceId: String = "",
    createDate: String = "",
): UserResponse
= UserResponse(
    id = id,
    subscriptionKey = subscriptionKey,
    deviceId = deviceId,
    createDate = createDate,
)

fun userControllerOf(
    getUserResult: CallResult<UserResponse> = Result.success(userResponseOf()),
    addUserResult: CallResult<UserResponse> = Result.success(userResponseOf()),
): UserController = object : UserController {
    override suspend fun getUser(subscriptionKey: SubscriptionKey?): CallResult<UserResponse> = getUserResult
    override suspend fun addUser(deviceId: String?): CallResult<UserResponse> = addUserResult
}
