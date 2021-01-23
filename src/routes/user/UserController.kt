package com.kamilh.routes.user

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.extensions.toIsoString
import com.kamilh.interactors.AddUser
import com.kamilh.interactors.AddUserParams
import com.kamilh.interactors.GetUser
import com.kamilh.interactors.GetUserParams
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.models.api.UserResponse
import com.kamilh.utils.toUUID
import io.ktor.http.*
import routes.CallError
import routes.CallResult

interface UserController {
    suspend fun getUser(subscriptionKey: SubscriptionKey?): CallResult<UserResponse>

    suspend fun addUser(deviceId: String?): CallResult<UserResponse>
}

class UserControllerImpl(
    private val getUser: GetUser,
    private val addUser: AddUser,
) : UserController {

    override suspend fun getUser(subscriptionKey: SubscriptionKey?): CallResult<UserResponse> =
        when (subscriptionKey) {
            null -> Result.failure(CallError(HttpStatusCode.Forbidden))
            else -> when (val result = getUser(GetUserParams(subscriptionKey))) {
                null -> Result.failure(userNotFound)
                else -> Result.success(result.toUserResponse())
            }
        }

    override suspend fun addUser(deviceId: String?): CallResult<UserResponse> =
        when (deviceId) {
            null -> Result.failure(deviceIdNotFound)
            else -> when (val uuid = deviceId.toUUID()) {
                null -> Result.failure(wrongDeviceId)
                else -> when (val result = addUser(AddUserParams(uuid))) {
                    is Result.Success -> getUser(result.value)
                    is Result.Failure -> {
                        Result.failure(userAlreadyExists)
                    }
                }
            }
        }
}

private val userNotFound: CallError = CallError(HttpStatusCode.NotFound)

private val wrongDeviceId: CallError = CallError(
    status = HttpStatusCode.BadRequest,
    message = "Device ID is expected to be in correct UUID format"
)

private val deviceIdNotFound: CallError = CallError(
    status = HttpStatusCode.BadRequest,
    message = "No deviceId query parameter found"
)

private val userAlreadyExists = CallError(
    status = HttpStatusCode.Conflict,
    message = "User already exists, please use different Device ID"
)

private fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = id,
        subscriptionKey = subscriptionKey.value.toString(),
        deviceId = deviceId.toString(),
        createDate = createDate.toIsoString(),
    )