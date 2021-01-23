package com.kamilh.interactors

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.AppDispatchers
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.models.appDispatchersOf
import com.kamilh.storage.*
import com.kamilh.utils.UuidCreator
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.util.*

class AddUserTest {

    @Test
    fun `test if userStorage insertResult is Failure and Error is SubscriptionKeyAlreadyInUse then Result is SubscriptionKeyAlreadyInUse`() = runBlockingTest {
        // GIVEN
        val insertResult: InsertUserResult = Result.failure(InsertUserError.SubscriptionKeyAlreadyInUse)

        // WHEN
        val result = addUserInteractorOf(
            userStorage = userStorageOf(
                insertResult = insertResult
            )
        )(AddUserParams(nullUUID()))

        // THEN
        require(result is Result.Failure)
        require(result.error == AddUserError.UserAlreadyExists)
    }

    @Test
    fun `test if userStorage insertResult is Failure and Error is DeviceIdAlreadyInUse then Result is DeviceIdAlreadyInUse`() = runBlockingTest {
        // GIVEN
        val insertResult: InsertUserResult = Result.failure(InsertUserError.DeviceIdAlreadyInUse)

        // WHEN
        val result = addUserInteractorOf(
            userStorage = userStorageOf(
                insertResult = insertResult
            )
        )(AddUserParams(nullUUID()))

        // THEN
        require(result is Result.Failure)
        require(result.error == AddUserError.UserAlreadyExists)
    }

    @Test
    fun `test if userStorage insertResult is Success then Result is Success`() = runBlockingTest {
        // GIVEN
        val uuid = UUID.randomUUID()
        val insertResult: InsertUserResult = Result.success(Unit)

        // WHEN
        val result = addUserInteractorOf(
            userStorage = userStorageOf(
                insertResult = insertResult,
            ),
            uuidCreator = uuidCreatorOf(uuid)
        )(AddUserParams(nullUUID()))

        // THEN
        require(result is Result.Success)
        assert(result.value.value == uuid)
    }
}

fun addUserInteractorOf(
    appDispatchers: AppDispatchers = appDispatchersOf(),
    userStorage: UserStorage = userStorageOf(),
    uuidCreator: UuidCreator = uuidCreatorOf(),
): AddUserInteractor =
    AddUserInteractor(
        appDispatchers = appDispatchers,
        userStorage = userStorage,
        uuidCreator = uuidCreator,
    )

fun userStorageOf(
    insertResult: InsertUserResult = Result.success(Unit),
    getUserResult: User? = null,
): UserStorage =
    object : UserStorage {
        override suspend fun insert(insertUser: InsertUser): InsertUserResult = insertResult
        override suspend fun getUser(subscriptionKey: SubscriptionKey): User? = getUserResult
    }

fun uuidCreatorOf(uuid: UUID = nullUUID()): UuidCreator = UuidCreator { uuid }