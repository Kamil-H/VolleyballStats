package com.kamilh.interactors

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.*
import com.kamilh.storage.InsertUser
import com.kamilh.storage.UserStorage
import com.kamilh.utils.UuidCreator
import java.time.OffsetDateTime
import java.util.*

typealias AddUser = Interactor<AddUserParams, AddUserResult>

data class AddUserParams(
    val deviceId: UUID,
)

typealias AddUserResult = Result<SubscriptionKey, AddUserError>

enum class AddUserError(override val message: String? = null): Error {
    UserAlreadyExists
}

class AddUserInteractor(
    appDispatchers: AppDispatchers,
    private val userStorage: UserStorage,
    private val uuidCreator: UuidCreator,
): AddUser(appDispatchers) {

    override suspend fun doWork(params: AddUserParams): AddUserResult {
        val subscriptionKey = SubscriptionKey(uuidCreator.create())
        val result = userStorage.insert(
            InsertUser(
                subscriptionKey = subscriptionKey,
                deviceId = params.deviceId,
                createDate = OffsetDateTime.now(),
            )
        )
        return result
            .map { subscriptionKey }
            .mapError { AddUserError.UserAlreadyExists }
    }
}