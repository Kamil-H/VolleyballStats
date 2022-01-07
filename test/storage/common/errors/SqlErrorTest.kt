package com.kamilh.storage.common.errors

import org.junit.Test

class SqlErrorTest {

    @Test
    fun `test if Exception doesn't contain message then createSqlError returns null`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val exception = Exception()

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        assert(error == null)
    }

    @Test
    fun `test if Exception is not of type it can detect and message doesn't contain column and table names then createSqlError returns null`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val exception = Exception("")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        assert(error == null)
    }

    @Test
    fun `test if exception contains correct table and column names and Exception is not of type it can detect then it is Unexpected`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val exception = Exception("$tableName.$columnName")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        require(error is SqlError.Unexpected)
        assert(error.exception == exception)
    }

    @Test
    fun `test if exception is SQLITE_CONSTRAINT_UNIQUE and correct table and column names are passed then createSqlError returns Uniqueness`() {
        // GIVEN
        val tableName = "user"
        val columnName = "device_id"
        val exception = Exception("[SQLITE_CONSTRAINT_UNIQUE]  A UNIQUE constraint failed (UNIQUE constraint failed: $tableName.$columnName)")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        assert(error is SqlError.Uniqueness)
    }

    @Test
    fun `test if exception is SQLITE_CONSTRAINT_UNIQUE and incorrect column name is passed then createSqlError returns null`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val searchedColumnName = "device_id"
        val exception = Exception("[SQLITE_CONSTRAINT_UNIQUE]  A UNIQUE constraint failed (UNIQUE constraint failed: $tableName.$columnName)")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = searchedColumnName,
        )

        // THEN
        assert(error == null)
    }

    @Test
    fun `test if exception is SQLITE_CONSTRAINT_PRIMARYKEY and incorrect column name is passed then createSqlError returns PrimaryKey`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val exception = Exception("[SQLITE_CONSTRAINT_PRIMARYKEY]  A UNIQUE constraint failed (UNIQUE constraint failed: $tableName.$columnName)")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        assert(error is SqlError.PrimaryKey)
    }

    @Test
    fun `test if exception is SQLITE_ERROR and incorrect column name is passed then createSqlError returns Error`() {
        // GIVEN
        val tableName = "user"
        val columnName = "id"
        val exception = Exception("[SQLITE_ERROR]  A UNIQUE constraint failed (UNIQUE constraint failed: $tableName.$columnName)")

        // WHEN
        val error = exception.createSqlError(
            tableName = tableName,
            columnName = columnName,
        )

        // THEN
        assert(error is SqlError.Error)
    }
}