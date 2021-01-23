package com.kamilh.interactors

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.AppDispatchers
import com.kamilh.models.User
import com.kamilh.storage.UserStorage

typealias GetUser = Interactor<GetUserParams, User?>

data class GetUserParams(
    val subscriptionKey: SubscriptionKey,
)

class GetUserInteractor(
    appDispatchers: AppDispatchers,
    private val userStorage: UserStorage,
): GetUser(appDispatchers) {

    override suspend fun doWork(params: GetUserParams): User? = userStorage.getUser(params.subscriptionKey)
}