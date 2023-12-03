package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Country
import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.PlayerPosition
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.migrations.League_model
import com.kamilh.volleyballstats.storage.migrations.Match_appearance_model
import com.kamilh.volleyballstats.storage.migrations.Match_model
import com.kamilh.volleyballstats.storage.migrations.Match_report_model
import com.kamilh.volleyballstats.storage.migrations.Play_attack_model
import com.kamilh.volleyballstats.storage.migrations.Play_model
import com.kamilh.volleyballstats.storage.migrations.Play_receive_model
import com.kamilh.volleyballstats.storage.migrations.Play_serve_model
import com.kamilh.volleyballstats.storage.migrations.Play_set_model
import com.kamilh.volleyballstats.storage.migrations.Player_model
import com.kamilh.volleyballstats.storage.migrations.Point_model
import com.kamilh.volleyballstats.storage.migrations.Set_model
import com.kamilh.volleyballstats.storage.migrations.Team_model
import com.kamilh.volleyballstats.storage.migrations.Team_player_model
import com.kamilh.volleyballstats.storage.migrations.Tour_model
import com.kamilh.volleyballstats.storage.migrations.Tour_team_model
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

interface DatabaseFactory {
    val database: Database

    fun connect()
    fun close()
}

@Inject
@Singleton
class AppConfigDatabaseFactory(
    sqlDriverFactory: SqlDriverFactory,
    zonedDateAdapter: ColumnAdapter<ZonedDateTime, String>,
    urlAdapter: ColumnAdapter<Url, String>,
    teamIdAdapter: ColumnAdapter<TeamId, Long>,
    playerIdAdapter: ColumnAdapter<PlayerId, Long>,
    countryAdapter: ColumnAdapter<Country, String>,
    localDateAdapter: ColumnAdapter<LocalDate, String>,
    localDateTimeAdapter: ColumnAdapter<LocalDateTime, String>,
    seasonAdapter: ColumnAdapter<Season, Long>,
    specializationAdapter: ColumnAdapter<Specialization, String>,
    durationAdapter: ColumnAdapter<Duration, Long>,
    positionAdapter: ColumnAdapter<PlayerPosition, Long>,
    matchIdAdapter: ColumnAdapter<MatchId, Long>,
    tourIdAdapter: ColumnAdapter<TourId, Long>,
    effectAdapter: ColumnAdapter<Effect, String>,
    phaseAdapter: ColumnAdapter<Phase, String>,
) : DatabaseFactory {

    private val driver: SqlDriver by lazy {
        sqlDriverFactory.create()
    }

    override val database: Database by lazy {
        Database(
            driver = driver,
            tour_team_modelAdapter = Tour_team_model.Adapter(
                image_urlAdapter = urlAdapter,
                logo_urlAdapter = urlAdapter,
                team_idAdapter = teamIdAdapter,
                updated_atAdapter = localDateTimeAdapter,
                tour_idAdapter = tourIdAdapter,
            ),
            player_modelAdapter = Player_model.Adapter(
                idAdapter = playerIdAdapter,
                birth_dateAdapter = localDateAdapter,
                updated_atAdapter = localDateTimeAdapter,
                heightAdapter = IntColumnAdapter,
                weightAdapter = IntColumnAdapter,
                rangeAdapter = IntColumnAdapter,
            ),
            team_player_modelAdapter = Team_player_model.Adapter(
                image_urlAdapter = urlAdapter,
                player_idAdapter = playerIdAdapter,
                updated_atAdapter = localDateTimeAdapter,
                specializationAdapter = specializationAdapter,
                numberAdapter = IntColumnAdapter,
            ),
            match_modelAdapter = Match_model.Adapter(
                dateAdapter = zonedDateAdapter,
                idAdapter = matchIdAdapter,
                tour_idAdapter = tourIdAdapter,
            ),
            point_modelAdapter = Point_model.Adapter(
                end_timeAdapter = zonedDateAdapter,
                start_timeAdapter = zonedDateAdapter,
                away_scoreAdapter = IntColumnAdapter,
                home_scoreAdapter = IntColumnAdapter,
            ),
            set_modelAdapter = Set_model.Adapter(
                end_timeAdapter = zonedDateAdapter,
                start_timeAdapter = zonedDateAdapter,
                match_idAdapter = matchIdAdapter,
                durationAdapter = durationAdapter,
                numberAdapter = IntColumnAdapter,
                home_scoreAdapter = IntColumnAdapter,
                away_scoreAdapter = IntColumnAdapter,
            ),
            tour_modelAdapter = Tour_model.Adapter(
                end_dateAdapter = localDateAdapter,
                start_dateAdapter = localDateAdapter,
                seasonAdapter = seasonAdapter,
                updated_atAdapter = localDateTimeAdapter,
                idAdapter = tourIdAdapter,
            ),
            league_modelAdapter = League_model.Adapter(
                countryAdapter = countryAdapter,
                divisionAdapter = IntColumnAdapter,
            ),
            team_modelAdapter = Team_model.Adapter(
                idAdapter = teamIdAdapter,
            ),
            match_appearance_modelAdapter = Match_appearance_model.Adapter(
                match_idAdapter = matchIdAdapter,
            ),
            match_report_modelAdapter = Match_report_model.Adapter(
                idAdapter = matchIdAdapter,
                phaseAdapter = phaseAdapter,
                updated_atAdapter = localDateTimeAdapter,
                tour_idAdapter = tourIdAdapter,
            ),
            play_attack_modelAdapter = Play_attack_model.Adapter(
                receive_effectAdapter = effectAdapter,
                set_effectAdapter = effectAdapter,
            ),
            play_modelAdapter = Play_model.Adapter(
                effectAdapter = effectAdapter,
                positionAdapter = positionAdapter,
                play_indexAdapter = IntColumnAdapter,
            ),
            play_receive_modelAdapter = Play_receive_model.Adapter(
                attack_effectAdapter = effectAdapter,
                set_effectAdapter = effectAdapter,
            ),
            play_serve_modelAdapter = Play_serve_model.Adapter(
                receiver_effectAdapter = effectAdapter,
            ),
            play_set_modelAdapter = Play_set_model.Adapter(
                attacker_positionAdapter = positionAdapter,
                attack_effectAdapter = effectAdapter,
            )
        )
    }

    override fun connect() {
        Database.Schema.create(driver)
    }

    override fun close() {
        driver.close()
    }
}
