package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.datetime.Clock
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.storage.common.adapters.*
import com.kamilh.volleyballstats.storage.databse.*
import com.kamilh.volleyballstats.utils.testClock
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration

internal expect fun driver(schema: SqlDriver.Schema, name: String): SqlDriver

abstract class DatabaseTest(
    private val zonedDateTimeAdapter: ColumnAdapter<ZonedDateTime, String> = ZonedDateTimeAdapter(),
    private val urlAdapter: ColumnAdapter<Url, String> = UrlAdapter(),
    private val teamIdAdapter: ColumnAdapter<TeamId, Long> = TeamIdAdapter(),
    private val playerIdAdapter: ColumnAdapter<PlayerId, Long> = PlayerIdAdapter(),
    private val countryAdapter: ColumnAdapter<Country, String> = CountryAdapter(),
    private val localDateAdapter: ColumnAdapter<LocalDate, String> = LocalDateAdapter(),
    private val localDateTimeAdapter: ColumnAdapter<LocalDateTime, String> = LocalDateTimeAdapter(),
    private val seasonAdapter: ColumnAdapter<Season, Long> = SeasonAdapter(),
    private val specializationAdapter: ColumnAdapter<Specialization, String> = SpecializationAdapter(),
    private val durationAdapter: ColumnAdapter<Duration, Long> = DurationAdapter(),
    private val positionAdapter: ColumnAdapter<PlayerPosition, Long> = PositionAdapter(),
    private val matchIdAdapter: ColumnAdapter<MatchId, Long> = MatchIdAdapter(),
    private val tourIdAdapter: ColumnAdapter<TourId, Long> = TourIdAdapter(),
    private val effectAdapter: ColumnAdapter<Effect, String> = EffectAdapter(),
    private val phaseAdapter: ColumnAdapter<Phase, String> = PhaseAdapter()
) {

    private lateinit var databaseFactory: DatabaseFactory
    protected val clock: Clock = testClock
    protected val teamQueries: TeamQueries by lazy { databaseFactory.database.teamQueries }
    protected val tourTeamQueries: TourTeamQueries by lazy { databaseFactory.database.tourTeamQueries }
    protected val tourQueries: TourQueries by lazy { databaseFactory.database.tourQueries }
    protected val leagueQueries: LeagueQueries by lazy { databaseFactory.database.leagueQueries }
    protected val teamPlayerQueries: TeamPlayerQueries by lazy { databaseFactory.database.teamPlayerQueries }
    protected val matchReportQueries: MatchReportQueries by lazy { databaseFactory.database.matchReportQueries }
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

    @BeforeTest
    fun setup() {
        databaseFactory = AppConfigDatabaseFactory(
            sqlDriverFactory = object : SqlDriverFactory {
                override fun create(): SqlDriver = driver(schema = Database.Schema, name = "test.db")
            },
            zonedDateAdapter = zonedDateTimeAdapter,
            urlAdapter = urlAdapter,
            teamIdAdapter = teamIdAdapter,
            playerIdAdapter = playerIdAdapter,
            countryAdapter = countryAdapter,
            localDateAdapter = localDateAdapter,
            localDateTimeAdapter = localDateTimeAdapter,
            seasonAdapter = seasonAdapter,
            specializationAdapter = specializationAdapter,
            durationAdapter = durationAdapter,
            positionAdapter = positionAdapter,
            matchIdAdapter = matchIdAdapter,
            tourIdAdapter = tourIdAdapter,
            effectAdapter = effectAdapter,
            phaseAdapter = phaseAdapter,
        )
        databaseFactory.connect()
    }

    @AfterTest
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
                id = tour.id,
                name = tour.name,
                season = tour.season,
                country = tour.league.country,
                division = tour.league.division,
                start_date = tour.startDate,
                end_date = tour.endDate,
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
                tour_id = insertTeam.tour.id,
                updated_at = insertTeam.team.updatedAt,
            )
        }
    }

    protected fun insert(vararg insertPlayers: InsertPlayer) = insert {
        insertPlayers.forEach { insertPlayer ->
            val player = insertPlayer.player
            playerQueries.insertPlayer(
                id = player.id,
                name = player.name,
                birth_date = player.date,
                height = player.height,
                weight = player.weight,
                range = player.weight,
                updated_at = player.updatedAt,
            )
            teamPlayerQueries.insertPlayer(
                image_url = player.imageUrl,
                tour_team_id = tourTeamQueries.selectId(
                    team_id = player.team,
                    tour_id = insertPlayer.tour.id,
                ).executeAsOne(),
                specialization = player.specialization,
                player_id = player.id,
                number = player.number,
                updated_at = player.updatedAt,
            )
        }
    }

    data class InsertTeam(
        val team: Team,
        val tour: Tour,
    )

    data class InsertPlayer(
        val player: Player,
        val tour: Tour,
    )
}