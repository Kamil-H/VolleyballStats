package com.kamilh.storage

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.models.Error
import com.kamilh.models.Result
import com.kamilh.models.User
import java.time.OffsetDateTime
import java.util.*

interface UserStorage {

    suspend fun insert(insertUser: InsertUser): InsertUserResult

    suspend fun getUser(subscriptionKey: SubscriptionKey): User?
}

data class InsertUser(
    val subscriptionKey: SubscriptionKey,
    val deviceId: UUID,
    val createDate: OffsetDateTime,
)

typealias InsertUserResult = Result<Unit, InsertUserError>

sealed class InsertUserError(override val message: String? = null): Error {
    object SubscriptionKeyAlreadyInUse: InsertUserError()
    object DeviceIdAlreadyInUse: InsertUserError()
}