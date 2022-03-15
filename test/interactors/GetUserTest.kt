package com.kamilh.interactors

import com.kamilh.models.AppDispatchers
import com.kamilh.models.appDispatchersOf
import com.kamilh.storage.UserStorage
import com.kamilh.storage.nullUUID
import com.kamilh.storage.subscriptionKeyOf
import com.kamilh.storage.userOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetUserTest {

    @Test
    fun `test if userStorage getUserResult returns null then null is returned`() = runTest {
        // GIVEN
        val getUserResult = null

        // WHEN
        val result = getUserInteractorOf(
            userStorage = userStorageOf(
                getUserResult = getUserResult
            )
        )(GetUserParams(subscriptionKeyOf(nullUUID())))

        // THEN
        assert(result == getUserResult)
    }

    @Test
    fun `test if userStorage getUserResult returns User then User is returned`() = runTest {
        // GIVEN
        val getUserResult = userOf()

        // WHEN
        val result = getUserInteractorOf(
            userStorage = userStorageOf(
                getUserResult = getUserResult
            )
        )(GetUserParams(subscriptionKeyOf(nullUUID())))

        // THEN
        assert(result == getUserResult)
    }
}

fun getUserInteractorOf(
    appDispatchers: AppDispatchers = appDispatchersOf(),
    userStorage: UserStorage = userStorageOf(),
): GetUserInteractor =
    GetUserInteractor(
        appDispatchers = appDispatchers,
        userStorage = userStorage,
    )