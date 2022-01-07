package com.kamilh.storage

import com.kamilh.databse.*
import com.kamilh.models.*
import com.kamilh.storage.common.adapters.*
import com.squareup.sqldelight.ColumnAdapter
import org.junit.After
import org.junit.Before
import storage.AppConfigDatabaseFactory
import storage.DatabaseFactory
import storage.common.adapters.OffsetDateAdapter
import java.time.*
import java.util.*

abstract class DatabaseTest(
    private val uuidAdapter: ColumnAdapter<UUID, String> = UuidAdapter(),
    private val offsetDateAdapter : ColumnAdapter<OffsetDateTime, String> = OffsetDateAdapter(),
    private val urlAdapter: ColumnAdapter<Url, String> = UrlAdapter(),
    private val teamIdAdapter: ColumnAdapter<TeamId, Long> = TeamIdAdapter(),
    private val playerIdAdapter: ColumnAdapter<PlayerId, Long> = PlayerIdAdapter(),
    private val countryAdapter: ColumnAdapter<Country, String> = CountryAdapter(),
    private val intAdapter: ColumnAdapter<Int, Long> = IntAdapter(),
    private val localDateAdapter: ColumnAdapter<LocalDate, String> = LocalDateAdapter(),
    private val localDateTimeAdapter: ColumnAdapter<LocalDateTime, String> = LocalDateTimeAdapter(),
    private val tourYearAdapter: ColumnAdapter<TourYear, Long> = TourYearAdapter(),
) {

    private lateinit var databaseFactory: DatabaseFactory
    protected val clock: Clock = Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("Z"))
    protected val userQueries: UserQueries by lazy { databaseFactory.database.userQueries }
    protected val teamQueries: TeamQueries by lazy { databaseFactory.database.teamQueries }
    protected val tourTeamQueries: TourTeamQueries by lazy { databaseFactory.database.tourTeamQueries }
    protected val tourQueries: TourQueries by lazy { databaseFactory.database.tourQueries }
    protected val leagueQueries: LeagueQueries by lazy { databaseFactory.database.leagueQueries }

    @Before
    fun setup() {
        databaseFactory = AppConfigDatabaseFactory(
            appConfig = TestAppConfig(),
            uuidAdapter = uuidAdapter,
            offsetDateAdapter = offsetDateAdapter,
            urlAdapter = urlAdapter,
            teamIdAdapter = teamIdAdapter,
            playerIdAdapter = playerIdAdapter,
            countryAdapter = countryAdapter,
            intAdapter = intAdapter,
            localDateAdapter = localDateAdapter,
            localDateTimeAdapter = localDateTimeAdapter,
            tourYearAdapter = tourYearAdapter,
        )
        databaseFactory.connect()
    }

    @After
    fun cleanup() {
        databaseFactory.close()
    }

    protected fun insert(league: League) {
        leagueQueries.insert(
            country = league.country,
            division = league.division,
        )
    }

    protected fun insert(tour: Tour) {
        tourQueries.insert(
            name = tour.name,
            tour_year = tour.year,
            country = tour.league.country,
            division = tour.league.division,
            start_date = tour.startDate,
            end_date = tour.endDate,
            winner_id = tour.winnerId,
            updated_at = tour.updatedAt,
        )
    }
}