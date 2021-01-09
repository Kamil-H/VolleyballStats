package com.kamilh.storage

import com.kamilh.Database
import com.kamilh.databse.User
import com.kamilh.databse.UserQueries
import com.kamilh.storage.common.adapters.UuidAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.After
import org.junit.Before
import storage.common.adapters.OffsetDateAdapter

abstract class DatabaseTest {

    private var inMemorySqlDriver: JdbcSqliteDriver? = null
    private var database: Database? = null

    protected val userQueries: UserQueries by lazy { database!!.userQueries }

    @Before
    fun setup() {
        val offsetDateAdapter = OffsetDateAdapter()
        val uuidAdapter = UuidAdapter()

        inMemorySqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            Database.Schema.create(this)
        }

        database = Database(
            driver = inMemorySqlDriver!!,
            userAdapter = User.Adapter(
                subscription_keyAdapter = uuidAdapter,
                device_idAdapter = uuidAdapter,
                dateAdapter = offsetDateAdapter,
            )
        )
    }

    @After
    fun cleanup() {
        inMemorySqlDriver!!.close()
        inMemorySqlDriver = null
        database = null
    }
}