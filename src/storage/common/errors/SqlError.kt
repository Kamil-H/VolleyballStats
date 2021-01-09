package com.kamilh.storage.common.errors

sealed class SqlError {
    object Uniqueness: SqlError()
    object PrimaryKey: SqlError()
    data class Unexpected(val exception: Exception): SqlError()
}

fun Exception.createSqlError(tableName: String, columnName: String): SqlError? =
    message?.let { message ->
        if (message.contains("$tableName.$columnName")) {
            when {
                message.contains("SQLITE_CONSTRAINT_PRIMARYKEY") -> SqlError.PrimaryKey
                message.contains("SQLITE_CONSTRAINT_UNIQUE") -> SqlError.Uniqueness
                else -> SqlError.Unexpected(this)
            }
        } else {
            null
        }
    }