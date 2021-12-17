package com.kamilh.storage

import com.kamilh.databse.TeamQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.databse.UserQueries
import com.kamilh.models.PlayerId
import com.kamilh.models.TeamId
import com.kamilh.models.TestAppConfig
import com.kamilh.models.Url
import com.kamilh.storage.common.adapters.PlayerIdAdapter
import com.kamilh.storage.common.adapters.TeamIdAdapter
import com.kamilh.storage.common.adapters.UrlAdapter
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
    private val urlAdapter: ColumnAdapter<Url, String> = UrlAdapter(),
    private val teamIdAdapter: ColumnAdapter<TeamId, Long> = TeamIdAdapter(),
    private val playerIdAdapter: ColumnAdapter<PlayerId, Long> = PlayerIdAdapter(),
) {

    private lateinit var databaseFactory: DatabaseFactory
    protected val userQueries: UserQueries by lazy { databaseFactory.database.userQueries }
    protected val teamQueries: TeamQueries by lazy { databaseFactory.database.teamQueries }
    protected val tourTeamQueries: TourTeamQueries by lazy { databaseFactory.database.tourTeamQueries }

    @Before
    fun setup() {
        databaseFactory = AppConfigDatabaseFactory(
            appConfig = TestAppConfig(),
            uuidAdapter = uuidAdapter,
            offsetDateAdapter = offsetDateAdapter,
            urlAdapter = urlAdapter,
            teamIdAdapter = teamIdAdapter,
            playerIdAdapter = playerIdAdapter,
        )
        databaseFactory.connect()
    }

    @After
    fun cleanup() {
        databaseFactory.close()
    }
}