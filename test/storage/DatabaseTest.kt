package com.kamilh.storage

import com.kamilh.databse.UserQueries
import com.kamilh.models.TestAppConfig
import com.kamilh.storage.common.adapters.UuidAdapter
import com.squareup.sqldelight.ColumnAdapter
import org.junit.After
import org.junit.Before
import storage.AppConfigDatabaseFactory
import storage.DatabaseFactory
import storage.common.adapters.OffsetDateAdapter
import java.time.OffsetDateTime
import java.util.*

abstract class DatabaseTest(
    private val uuidAdapter: ColumnAdapter<UUID, String> = UuidAdapter(),
    private val offsetDateAdapter : ColumnAdapter<OffsetDateTime, String> = OffsetDateAdapter(),
) {

    private lateinit var databaseFactory: DatabaseFactory
    protected val userQueries: UserQueries by lazy { databaseFactory.database.userQueries }

    @Before
    fun setup() {
        databaseFactory = AppConfigDatabaseFactory(
            appConfig = TestAppConfig(),
            uuidAdapter = uuidAdapter,
            offsetDateAdapter = offsetDateAdapter,
        )
        databaseFactory.connect()
    }

    @After
    fun cleanup() {
        databaseFactory.close()
    }
}