package com.kamilh.storage

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.databse.UserQueries
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.storage.common.QueryRunner

class SqlUserStorage(
    private val queryRunner: QueryRunner,
    private val userQueries: UserQueries,
) : UserStorage {

    override suspend fun insert(insertUser: InsertUser): InsertUserResult =
        queryRunner.run {
            try {
                Result.success(
                    userQueries.insertUser(
                        subscription_key = insertUser.subscriptionKey.value,
                        device_id = insertUser.deviceId,
                        date = insertUser.createDate,
                    )
                )
            } catch (exception: Exception) {
                when {
                    exception.checkIfContains(tableName = "user", columnName = "subscription_key") ->
                        Result.failure(InsertUserError.SubscriptionKeyAlreadyInUse)
                    exception.checkIfContains(tableName = "user", columnName = "device_id") ->
                        Result.failure(InsertUserError.DeviceIdAlreadyInUse)
                    else -> throw exception
                }
            }
        }

    override suspend fun getUser(subscriptionKey: SubscriptionKey): User? =
        queryRunner.run {
            userQueries.selectAllUsersBySubscriptionKey(subscriptionKey.value).executeAsOneOrNull()?.toUser()
        }
}

private fun Exception.checkIfContains(tableName: String, columnName: String): Boolean =
    message?.let { message ->
        val subject = "$tableName($columnName)"
        message.contains(subject, ignoreCase = true)
    } ?: false

private fun com.kamilh.databse.User.toUser(): User =
    User(
        id = id.toLong(),
        subscriptionKey = SubscriptionKey(subscription_key),
        deviceId = device_id,
        createDate = date,
    )