package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.kamilh.volleyballstats.datetime.Clock
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.Country
import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.Player
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.PlayerPosition
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.common.adapters.CountryAdapter
import com.kamilh.volleyballstats.storage.common.adapters.DurationAdapter
import com.kamilh.volleyballstats.storage.common.adapters.EffectAdapter
import com.kamilh.volleyballstats.storage.common.adapters.LocalDateAdapter
import com.kamilh.volleyballstats.storage.common.adapters.LocalDateTimeAdapter
import com.kamilh.volleyballstats.storage.common.adapters.MatchIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PhaseAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PlayerIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PositionAdapter
import com.kamilh.volleyballstats.storage.common.adapters.SeasonAdapter
import com.kamilh.volleyballstats.storage.common.adapters.SpecializationAdapter
import com.kamilh.volleyballstats.storage.common.adapters.TeamIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.TourIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.UrlAdapter
import com.kamilh.volleyballstats.storage.common.adapters.ZonedDateTimeAdapter
import com.kamilh.volleyballstats.storage.databse.LeagueQueries
import com.kamilh.volleyballstats.storage.databse.MatchAppearanceQueries
import com.kamilh.volleyballstats.storage.databse.MatchQueries
import com.kamilh.volleyballstats.storage.databse.MatchReportQueries
import com.kamilh.volleyballstats.storage.databse.PlayAttackQueries
import com.kamilh.volleyballstats.storage.databse.PlayBlockQueries
import com.kamilh.volleyballstats.storage.databse.PlayDigQueries
import com.kamilh.volleyballstats.storage.databse.PlayFreeballQueries
import com.kamilh.volleyballstats.storage.databse.PlayQueries
import com.kamilh.volleyballstats.storage.databse.PlayReceiveQueries
import com.kamilh.volleyballstats.storage.databse.PlayServeQueries
import com.kamilh.volleyballstats.storage.databse.PlaySetQueries
import com.kamilh.volleyballstats.storage.databse.PlayerQueries
import com.kamilh.volleyballstats.storage.databse.PointLineupQueries
import com.kamilh.volleyballstats.storage.databse.PointQueries
import com.kamilh.volleyballstats.storage.databse.SetQueries
import com.kamilh.volleyballstats.storage.databse.TeamPlayerQueries
import com.kamilh.volleyballstats.storage.databse.TeamQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import com.kamilh.volleyballstats.storage.databse.TourTeamQueries
import com.kamilh.volleyballstats.utils.testClock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration

internal expect fun <T : QueryResult.Value<Unit>> driver(schema: SqlSchema<T>, name: String): SqlDriver

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
