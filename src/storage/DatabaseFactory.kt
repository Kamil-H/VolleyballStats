package storage

import com.kamilh.*
import com.kamilh.models.*
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration

interface DatabaseFactory {
	val database: Database

	fun connect()
	fun close()
}

interface SqlDriverCreator {
	fun create(): SqlDriver
}

internal class AppConfigDatabaseFactory(
	appConfig: AppConfig,
	uuidAdapter: ColumnAdapter<UUID, String>,
	offsetDateAdapter: ColumnAdapter<OffsetDateTime, String>,
	urlAdapter: ColumnAdapter<Url, String>,
	teamIdAdapter: ColumnAdapter<TeamId, Long>,
	playerIdAdapter: ColumnAdapter<PlayerId, Long>,
    countryAdapter: ColumnAdapter<Country, String>,
    localDateAdapter: ColumnAdapter<LocalDate, String>,
    localDateTimeAdapter: ColumnAdapter<LocalDateTime, String>,
    tourYearAdapter: ColumnAdapter<TourYear, Long>,
	specializationAdapter: ColumnAdapter<Player.Specialization, Long>,
	matchReportIdAdapter: ColumnAdapter<MatchReportId, Long>,
	durationAdapter: ColumnAdapter<Duration, Long>,
	phaseAdapter: ColumnAdapter<Phase, String>,
	effectAdapter: ColumnAdapter<Effect, String>,
	positionAdapter: ColumnAdapter<PlayerPosition, Long>,
	matchIdAdapter: ColumnAdapter<MatchId, Long>,
): DatabaseFactory {

	private val driver: SqlDriver by lazy {
		JdbcSqliteDriver(url = appConfig.databaseConfig.jdbcUrl)
	}

	override val database: Database by lazy {
		Database(
			driver = driver,
			user_modelAdapter = User_model.Adapter(
				created_dateAdapter = offsetDateAdapter,
				subscription_keyAdapter = uuidAdapter,
				device_idAdapter = uuidAdapter,
			),
			tour_team_modelAdapter = Tour_team_model.Adapter(
				image_urlAdapter = urlAdapter,
				logo_urlAdapter = urlAdapter,
				team_idAdapter = teamIdAdapter,
				updated_atAdapter = localDateTimeAdapter,
			),
			player_modelAdapter = Player_model.Adapter(
				idAdapter = playerIdAdapter,
				birth_dateAdapter = localDateAdapter,
				updated_atAdapter = localDateTimeAdapter,
			),
			team_player_modelAdapter = Team_player_model.Adapter(
				image_urlAdapter = urlAdapter,
				player_idAdapter = playerIdAdapter,
				updated_atAdapter = localDateTimeAdapter,
				positionAdapter = specializationAdapter,
			),
			match_modelAdapter = Match_model.Adapter(
				dateAdapter = localDateTimeAdapter,
				match_statistics_idAdapter = matchReportIdAdapter,
				idAdapter = matchIdAdapter,
				stateAdapter = EnumColumnAdapter(),
			),
			point_modelAdapter = Point_model.Adapter(
				end_timeAdapter = offsetDateAdapter,
				start_timeAdapter = offsetDateAdapter,
			),
			set_modelAdapter = Set_model.Adapter(
				end_timeAdapter = offsetDateAdapter,
				start_timeAdapter = offsetDateAdapter,
				match_statistics_idAdapter = matchReportIdAdapter,
				durationAdapter = durationAdapter,
			),
			tour_modelAdapter = Tour_model.Adapter(
				end_dateAdapter = localDateAdapter,
				start_dateAdapter = localDateAdapter,
                tour_yearAdapter = tourYearAdapter,
                updated_atAdapter = localDateTimeAdapter,
                winner_idAdapter = teamIdAdapter,
			),
			league_modelAdapter = League_model.Adapter(
				countryAdapter = countryAdapter,
			),
			team_modelAdapter = Team_model.Adapter(
				idAdapter = teamIdAdapter,
			),
			match_appearance_modelAdapter = Match_appearance_model.Adapter(
				match_statistics_idAdapter = matchReportIdAdapter,
			),
			match_statistics_modelAdapter = Match_statistics_model.Adapter(
				idAdapter = matchReportIdAdapter,
				phaseAdapter = phaseAdapter,
				updated_atAdapter = localDateTimeAdapter,
			),
			play_attack_modelAdapter = Play_attack_model.Adapter(
				receive_effectAdapter = effectAdapter,
				set_effectAdapter = effectAdapter,
			),
			play_modelAdapter = Play_model.Adapter(
				effectAdapter = effectAdapter,
				positionAdapter = positionAdapter,
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