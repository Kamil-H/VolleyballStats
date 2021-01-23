package com.kamilh.routes

import com.kamilh.interactors.AddUser
import com.kamilh.interactors.AddUserError
import com.kamilh.interactors.AddUserResult
import com.kamilh.interactors.GetUser
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.models.interactorOf
import com.kamilh.routes.user.UserControllerImpl
import com.kamilh.storage.nullUUID
import com.kamilh.storage.subscriptionKeyOf
import com.kamilh.storage.userOf
import io.ktor.http.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class UserControllerTest {

    @Test
    fun `test that getUser returns Failure NotFound when getUser returns null`() = runBlockingTest {
        // GIVEN
        val getUserResult = null

        // WHEN
        val result = userControllerImplOf(getUserResult = getUserResult).getUser(subscriptionKeyOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error.status == HttpStatusCode.NotFound)
    }

    @Test
    fun `test that getUser returns Failure Forbidden when getUser returns user but subscription key is null`() = runBlockingTest {
        // GIVEN
        val getUserResult = userOf()

        // WHEN
        val result = userControllerImplOf(getUserResult = getUserResult).getUser(null)

        // THEN
        require(result is Result.Failure)
        assert(result.error.status == HttpStatusCode.Forbidden)
    }

    @Test
    fun `test that getUser returns Success when getUser returns user and subscription key is not null`() = runBlockingTest {
        // GIVEN
        val getUserResult = userOf()

        // WHEN
        val result = userControllerImplOf(getUserResult = getUserResult).getUser(subscriptionKeyOf())

        // THEN
        require(result is Result.Success)
    }

    @Test
    fun `test that addUser returns Failure BadRequest when passed deviceId is null`() = runBlockingTest {
        // GIVEN
        val deviceId = null

        // WHEN
        val result = userControllerImplOf().addUser(deviceId)

        // THEN
        require(result is Result.Failure)
        assert(result.error.status == HttpStatusCode.BadRequest)
    }

    @Test
    fun `test that addUser returns Failure BadRequest when passed deviceId is not null, but is incorrect UUID`() = runBlockingTest {
        // GIVEN
        val deviceId = ""

        // WHEN
        val result = userControllerImplOf().addUser(deviceId)

        // THEN
        require(result is Result.Failure)
        assert(result.error.status == HttpStatusCode.BadRequest)
    }

    @Test
    fun `test that addUser returns Failure Conflict when addUser returns Error`() = runBlockingTest {
        // GIVEN
        val deviceId = nullUUID().toString()
        val addUserResult: AddUserResult = AddUserResult.failure(AddUserError.UserAlreadyExists)

        // WHEN
        val result = userControllerImplOf(addUserResult = addUserResult).addUser(deviceId)

        // THEN
        require(result is Result.Failure)
        assert(result.error.status == HttpStatusCode.Conflict)
    }

    @Test
    fun `test that addUser returns Failure when getUser returns Failure`() = runBlockingTest {
        // GIVEN
        val deviceId = nullUUID().toString()
        val addUserResult: AddUserResult = AddUserResult.failure(AddUserError.UserAlreadyExists)
        val getUserResult = userOf()

        // WHEN
        val result = userControllerImplOf(
            getUserResult = getUserResult,
            addUserResult = addUserResult
        ).addUser(deviceId)

        // THEN
        require(result is Result.Failure)
    }

    @Test
    fun `test that addUser returns Success when addUser returns Success`() = runBlockingTest {
        // GIVEN
        val deviceId = nullUUID().toString()
        val subscriptionKey = subscriptionKeyOf()
        val addUserResult: AddUserResult = AddUserResult.success(subscriptionKey)
        val getUserResult = userOf()

        // WHEN
        val result = userControllerImplOf(
            getUserResult = getUserResult,
            addUserResult = addUserResult
        ).addUser(deviceId)

        // THEN
        require(result is Result.Success)
        assert(result.value.subscriptionKey == subscriptionKey.toString())
    }
}

private fun userControllerImplOf(
    getUserResult: User? = null,
    addUserResult: AddUserResult = AddUserResult.success(subscriptionKeyOf())
): UserControllerImpl =
    UserControllerImpl(
        getUser = getUserOf(getUserResult),
        addUser = addUserOf(addUserResult)
    )

fun getUserOf(
    user: User? = null
): GetUser = interactorOf(user)

fun addUserOf(
    addUserResult: AddUserResult = AddUserResult.success(subscriptionKeyOf())
): AddUser = interactorOf(addUserResult)