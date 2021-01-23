package com.kamilh.interactors

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.AppDispatchers
import com.kamilh.storage.UserStorage

typealias SubscriptionKeyValidator = Interactor<SubscriptionKeyValidatorParams, SubscriptionKeyValidatorResult>

data class SubscriptionKeyValidatorParams(
    val subscriptionKey: SubscriptionKey,
)

enum class SubscriptionKeyValidatorResult {
    Valid, Invalid
}

class SubscriptionKeyValidatorInteractor(
    appDispatchers: AppDispatchers,
    private val userStorage: UserStorage,
): SubscriptionKeyValidator(appDispatchers) {

    override suspend fun doWork(params: SubscriptionKeyValidatorParams): SubscriptionKeyValidatorResult =
        if (userStorage.getUser(params.subscriptionKey) != null) {
            SubscriptionKeyValidatorResult.Valid
        } else {
            SubscriptionKeyValidatorResult.Invalid
        }
}