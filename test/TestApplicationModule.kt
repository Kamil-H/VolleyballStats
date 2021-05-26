package com.kamilh

import com.kamilh.authorization.CredentialsValidator
import com.kamilh.authorization.SubscriptionKey
import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.Result
import com.kamilh.models.api.UserResponse
import com.kamilh.routes.user.UserController
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import routes.CallResult

private const val MODULE_NAME = "DI_TEST_APPLICATION_MODULE"
fun testApplicationModule(
    userController: UserController = userControllerOf(),
    credentialsValidator: CredentialsValidator = credentialsValidatorOf(),
) = DI.Module(name = MODULE_NAME) {

    bind<Json>() with provider { Json {  } }
    bind<SqlDriver>() with provider { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }
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
