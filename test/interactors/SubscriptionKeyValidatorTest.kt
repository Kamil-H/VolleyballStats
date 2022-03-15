package com.kamilh.interactors

import com.kamilh.models.AppDispatchers
import com.kamilh.models.appDispatchersOf
import com.kamilh.storage.UserStorage
import com.kamilh.storage.nullUUID
import com.kamilh.storage.subscriptionKeyOf
import com.kamilh.storage.userOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SubscriptionKeyValidatorTest {

    @Test
    fun `test if userStorage's getUserResult returns null then Invalid is returned`() = runTest {
        // GIVEN
        val getUserResult = null

        // WHEN
        val result = subscriptionKeyValidatorOf(
            userStorage = userStorageOf(
                getUserResult = getUserResult
            )
        )(SubscriptionKeyValidatorParams(subscriptionKeyOf(nullUUID())))

        // THEN
        assert(result == SubscriptionKeyValidatorResult.Invalid)
    }

    @Test
    fun `test if userStorage's getUserResult returns User then Valid is returned`() = runTest {
        // GIVEN
        val getUserResult = userOf()

        // WHEN
        val result = subscriptionKeyValidatorOf(
            userStorage = userStorageOf(
                getUserResult = getUserResult
            )
        )(SubscriptionKeyValidatorParams(subscriptionKeyOf(nullUUID())))

        // THEN
        assert(result == SubscriptionKeyValidatorResult.Valid)
    }
}

fun subscriptionKeyValidatorOf(
    appDispatchers: AppDispatchers = appDispatchersOf(),
    userStorage: UserStorage = userStorageOf(),
): SubscriptionKeyValidatorInteractor =
    SubscriptionKeyValidatorInteractor(
        appDispatchers = appDispatchers,
        userStorage = userStorage,
    )