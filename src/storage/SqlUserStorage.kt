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
                        created_date = insertUser.createDate,
                    )
                )
            } catch (exception: Exception) {
                when {
                    exception.checkIfContains(tableName = "user_model", columnName = "subscription_key") ->
                        Result.failure(InsertUserError.SubscriptionKeyAlreadyInUse)
                    exception.checkIfContains(tableName = "user_model", columnName = "device_id") ->
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
        val subject = "$tableName($columnName"
        message.contains(subject, ignoreCase = true)
    } ?: false

private fun com.kamilh.User_model.toUser(): User =
    User(
        id = id,
        subscriptionKey = SubscriptionKey(subscription_key),
        deviceId = device_id,
        createDate = created_date,
    )