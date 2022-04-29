package com.kamilh

import com.kamilh.authorization.CredentialsValidator
import com.kamilh.authorization.SubscriptionKey
import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.Result
import com.kamilh.models.api.user.UserResponse
import com.kamilh.routes.RoutesModule
import com.kamilh.routes.user.UserController
import routes.CallResult

fun routesModuleOf(
    userController: UserController = userControllerOf(),
    credentialsValidator: CredentialsValidator = credentialsValidatorOf(),
): RoutesModule = object : RoutesModule {
    override val userController: UserController = userController
    override val credentialsValidator: CredentialsValidator = credentialsValidator
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
