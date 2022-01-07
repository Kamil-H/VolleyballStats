package com.kamilh.storage

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.storage.common.QueryRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlUserStorageTest : DatabaseTest() {

    private val userStorage: UserStorage by lazy {
        SqlUserStorage(
            queryRunner = testQueryRunner,
            userQueries = userQueries
        )
    }

    @Test
    fun `test if user is getting inserted`() = runBlockingTest {
        // GIVEN
        val user = insertUserOf()

        // WHEN
        val result = userStorage.insert(user)

        // THEN
        assertTrue(result is Result.Success)
        assertTrue(userQueries.selectAllUsers().executeAsList().isNotEmpty())
    }

    @Test
    fun `test if user is getting not inserted when user with the same subscriptionKey is already added`() = runBlockingTest {
        // GIVEN
        val firstUser = insertUserOf(deviceId = nullUUID())
        val secondUser = insertUserOf(deviceId = UUID.randomUUID())

        // WHEN
        val firstResult = userStorage.insert(firstUser)
        val secondResult = userStorage.insert(secondUser)

        // THEN
        assertTrue(firstResult is Result.Success)
        require(secondResult is Result.Failure)
        assertEquals(InsertUserError.SubscriptionKeyAlreadyInUse, secondResult.error)
    }

    @Test
    fun `test if user is getting not inserted when user with the same device_id is already added`() = runBlockingTest {
        // GIVEN
        val deviceId = UUID.randomUUID()
        val firstUser = insertUserOf(
            subscriptionKey = nullUUID(),
            deviceId = deviceId,
        )
        val secondUser = insertUserOf(
            subscriptionKey = UUID.randomUUID(),
            deviceId = deviceId,
        )

        // WHEN
        val firstResult = userStorage.insert(firstUser)
        val secondResult = userStorage.insert(secondUser)

        // THEN
        assertTrue(firstResult is Result.Success)
        require(secondResult is Result.Failure)
        assertEquals(InsertUserError.DeviceIdAlreadyInUse, secondResult.error)
    }

    @Test
    fun `test if user is returned when it's available in database`() = runBlockingTest {
        // GIVEN
        val subscriptionKey = nullUUID()
        val user = insertUserOf(subscriptionKey = subscriptionKey)

        // WHEN
        userStorage.insert(user)
        val result = userStorage.getUser(subscriptionKeyOf(subscriptionKey))

        // THEN
        assertTrue(result != null)
    }

    @Test
    fun `test if null is returned when it's not available in database`() = runBlockingTest {
        // GIVEN
        val subscriptionKey = subscriptionKeyOf(nullUUID())

        // WHEN
        val result = userStorage.getUser(subscriptionKey)

        // THEN
        assertTrue(result == null)
    }

    @Test
    fun `test if null is returned when searched id is not available in database`() = runBlockingTest {
        // GIVEN
        val subscriptionKey = UUID.randomUUID()
        val user = insertUserOf(subscriptionKey = subscriptionKey)

        // WHEN
        userStorage.insert(user)
        val result = userStorage.getUser(subscriptionKeyOf(nullUUID()))

        // THEN
        assertTrue(result == null)
    }
}

fun nullUUID(): UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

fun userOf(
    id: Long = 0L,
    subscriptionKey: UUID = nullUUID(),
    deviceId: UUID = nullUUID(),
    createDate: OffsetDateTime = OffsetDateTime.now(),
): User =
    User(
        id = id,
        subscriptionKey = subscriptionKeyOf(subscriptionKey),
        deviceId = deviceId,
        createDate = createDate,
    )

fun insertUserOf(
    subscriptionKey: UUID = nullUUID(),
    deviceId: UUID = nullUUID(),
    createDate: OffsetDateTime = OffsetDateTime.now(),
): InsertUser =
    InsertUser(
        subscriptionKey = subscriptionKeyOf(subscriptionKey),
        deviceId = deviceId,
        createDate = createDate,
    )

fun subscriptionKeyOf(
    uuid: UUID = nullUUID()
): SubscriptionKey =
    SubscriptionKey(value = uuid)

class TestQueryRunner(override val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined) : QueryRunner {
    override suspend fun <T> run(body: () -> T): T = body()
    override suspend fun <T> runTransaction(body: () -> T): T = body()
}

val testQueryRunner: TestQueryRunner = TestQueryRunner()