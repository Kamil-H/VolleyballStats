package com.kamilh.storage

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.databse.UserQueries
import com.kamilh.models.Result
import com.kamilh.models.User
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError

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
                val subscriptionKeyError = exception.createSqlError(tableName = "user", columnName = "subscription_key")
                val deviceIdError = exception.createSqlError(tableName = "user", columnName = "device_id")
                when {
                    subscriptionKeyError is SqlError.Uniqueness -> Result.failure(InsertUserError.SubscriptionKeyAlreadyInUse)
                    deviceIdError is SqlError.Uniqueness -> Result.failure(InsertUserError.DeviceIdAlreadyInUse)
                    else -> throw exception
                }
            }
        }

    override suspend fun getUser(subscriptionKey: SubscriptionKey): User? =
        queryRunner.run {
            userQueries.selectAllUsersBySubscriptionKey(subscriptionKey.value).executeAsOneOrNull()?.toUser()
        }
}

private fun com.kamilh.databse.User.toUser(): User =
    User(
        id = id,
        subscriptionKey = SubscriptionKey(subscription_key),
        deviceId = device_id,
        createDate = date,
    )