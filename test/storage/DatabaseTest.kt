package com.kamilh.storage

import com.kamilh.databse.*
import com.kamilh.models.*
import com.kamilh.storage.common.adapters.*
import com.kamilh.utils.testClock
import com.squareup.sqldelight.ColumnAdapter
import models.PlayerWithDetails
import org.junit.After
import org.junit.Before
import storage.AppConfigDatabaseFactory
import storage.DatabaseFactory
import storage.common.adapters.OffsetDateAdapter
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration

abstract class DatabaseTest(
    private val uuidAdapter: ColumnAdapter<UUID, String> = UuidAdapter(),
    private val offsetDateAdapter: ColumnAdapter<OffsetDateTime, String> = OffsetDateAdapter(),
    private val urlAdapter: ColumnAdapter<Url, String> = UrlAdapter(),
    private val teamIdAdapter: ColumnAdapter<TeamId, Long> = TeamIdAdapter(),
    private val playerIdAdapter: ColumnAdapter<PlayerId, Long> = PlayerIdAdapter(),
    private val countryAdapter: ColumnAdapter<Country, String> = CountryAdapter(),
    private val localDateAdapter: ColumnAdapter<LocalDate, String> = LocalDateAdapter(),
    private val localDateTimeAdapter: ColumnAdapter<LocalDateTime, String> = LocalDateTimeAdapter(),
    private val tourYearAdapter: ColumnAdapter<TourYear, Long> = TourYearAdapter(),
    private val specializationAdapter: ColumnAdapter<TeamPlayer.Specialization, Long> = SpecializationAdapter(),
    private val matchReportIdAdapter: ColumnAdapter<MatchReportId, Long> = MatchReportIdAdapter(),
    private val durationAdapter: ColumnAdapter<Duration, Long> = DurationAdapter(),
    private val positionAdapter: ColumnAdapter<PlayerPosition, Long> = PositionAdapter(),
    private val matchIdAdapter: ColumnAdapter<MatchId, Long> = MatchIdAdapter(),
) {

    private lateinit var databaseFactory: DatabaseFactory
    protected val clock: Clock = testClock
    protected val userQueries: UserQueries by lazy { databaseFactory.database.userQueries }
    protected val teamQueries: TeamQueries by lazy { databaseFactory.database.teamQueries }
    protected val tourTeamQueries: TourTeamQueries by lazy { databaseFactory.database.tourTeamQueries }
    protected val tourQueries: TourQueries by lazy { databaseFactory.database.tourQueries }
    protected val leagueQueries: LeagueQueries by lazy { databaseFactory.database.leagueQueries }
    protected val teamPlayerQueries: TeamPlayerQueries by lazy { databaseFactory.database.teamPlayerQueries }
    protected val matchStatisticsQueries: MatchStatisticsQueries by lazy { databaseFactory.database.matchStatisticsQueries }
    protected val playQueries: PlayQueries by lazy { databaseFactory.database.playQueries }
    protected val playerQueries: PlayerQueries by lazy { databaseFactory.database.playerQueries }
    protected val playAttackQueries: PlayAttackQueries by lazy { databaseFactory.database.playAttackQueries }
    protected val playBlockQueries: PlayBlockQueries by lazy { databaseFactory.database.playBlockQueries }
    protected val playDigQueries: PlayDigQueries by lazy { databaseFactory.database.playDigQueries }
    protected val playFreeballQueries: PlayFreeballQueries by lazy { databaseFactory.database.playFreeballQueries }
    protected val playReceiveQueries: PlayReceiveQueries by lazy { databaseFactory.database.playReceiveQueries }
    protected val playServeQueries: PlayServeQueries by lazy { databaseFactory.database.playServeQueries }
    protected val playSetQueries: PlaySetQueries by lazy { databaseFactory.database.playSetQueries }
    protected val pointQueries: PointQueries by lazy { databaseFactory.database.pointQueries }
    protected val pointLineupQueries: PointLineupQueries by lazy { databaseFactory.database.pointLineupQueries }
    protected val setQueries: SetQueries by lazy { databaseFactory.database.setQueries }
    protected val matchAppearanceQueries: MatchAppearanceQueries by lazy { databaseFactory.database.matchAppearanceQueries }
    protected val matchQueries: MatchQueries by lazy { databaseFactory.database.matchQueries }

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
            localDateAdapter = localDateAdapter,
            localDateTimeAdapter = localDateTimeAdapter,
            tourYearAdapter = tourYearAdapter,
            specializationAdapter = specializationAdapter,
            matchReportIdAdapter = matchReportIdAdapter,
            durationAdapter = durationAdapter,
            positionAdapter = positionAdapter,
            matchIdAdapter = matchIdAdapter,
        )
        databaseFactory.connect()
    }

    @After
    fun cleanup() {
        databaseFactory.close()
    }

    private fun insert(insertAction: () -> Unit) {
        try {
            insertAction()
        } catch (exception: Exception) {
            println(exception.message)
        }
    }

    protected fun insert(vararg leagues: League) = insert {
        leagues.forEach { league ->
            leagueQueries.insert(
                country = league.country,
                division = league.division,
            )
        }
    }

    protected fun insert(vararg tours: Tour) = insert {
        tours.forEach { tour ->
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

    protected fun insert(vararg insertTeams: InsertTeam) = insert {
        insertTeams.forEach { insertTeam ->
            teamQueries.insert(insertTeam.team.id)
            tourTeamQueries.insert(
                name = insertTeam.team.name,
                image_url = insertTeam.team.teamImageUrl,
                logo_url = insertTeam.team.logoUrl,
                team_id = insertTeam.team.id,
                tour_year = insertTeam.tourYear,
                updated_at = insertTeam.team.updatedAt,
                country = insertTeam.league.country,
                division = insertTeam.league.division,
            )
        }
    }

    protected fun insert(vararg insertPlayers: InsertPlayer) = insert {
        insertPlayers.forEach { insertPlayer ->
            val player = insertPlayer.player
            playerQueries.insertPlayer(
                id = player.teamPlayer.id,
                name = player.teamPlayer.name,
                birth_date = player.details.date,
                height = player.details.height,
                weight = player.details.weight,
                range = player.details.weight,
                updated_at = player.details.updatedAt,
            )
            teamPlayerQueries.insertPlayer(
                image_url = player.teamPlayer.imageUrl,
                tour_team_id = tourTeamQueries.selectId(
                    team_id = player.teamPlayer.team,
                    tour_id = tourQueries.selectId(
                        tour_year = insertPlayer.tourYear,
                        division = insertPlayer.league.division,
                        country = insertPlayer.league.country,
                    ).executeAsOne()
                ).executeAsOne(),
                specialization = player.teamPlayer.specialization,
                player_id = player.teamPlayer.id,
                number = player.details.number,
                updated_at = player.teamPlayer.updatedAt,
            )
        }
    }

    data class InsertTeam(
        val team: Team,
        val league: League,
        val tourYear: TourYear,
    )

    data class InsertPlayer(
        val player: PlayerWithDetails,
        val league: League,
        val tourYear: TourYear,
    )
}