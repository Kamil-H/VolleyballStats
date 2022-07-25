package com.kamilh.volleyballstats.storage.common.errors

sealed class SqlError {
    object Uniqueness : SqlError()
    object PrimaryKey : SqlError()
    object Error : SqlError()
    object NotNull : SqlError()
    data class Unexpected(val exception: Exception) : SqlError()
}

fun Exception.createSqlError(tableName: String, columnName: String): SqlError? =
    message?.let { message ->
        if (message.contains("$tableName.$columnName")) {
            when {
                message.contains("SQLITE_ERROR") -> SqlError.Error
                message.contains("SQLITE_CONSTRAINT_PRIMARYKEY") -> SqlError.PrimaryKey
                message.contains("SQLITE_CONSTRAINT_UNIQUE") -> SqlError.Uniqueness
                message.contains("SQLITE_CONSTRAINT_NOTNULL") -> SqlError.NotNull
                else -> SqlError.Unexpected(this)
            }
        } else {
            null
        }
    }
