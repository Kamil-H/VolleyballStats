package com.kamilh.volleyballstats.storage

import com.kamilh.*
import com.kamilh.volleyballstats.Singleton
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.models.*
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

interface DatabaseFactory {
	val database: Database

	fun connect()
	fun close()
}

interface SqlDriverCreator {
	fun create(): SqlDriver
}

@Inject
@Singleton
class AppConfigDatabaseFactory(
	appConfig: AppConfig,
	zonedDateAdapter: ColumnAdapter<ZonedDateTime, String>,
	urlAdapter: ColumnAdapter<Url, String>,
	teamIdAdapter: ColumnAdapter<TeamId, Long>,
	playerIdAdapter: ColumnAdapter<PlayerId, Long>,
	countryAdapter: ColumnAdapter<Country, String>,
	localDateAdapter: ColumnAdapter<LocalDate, String>,
	localDateTimeAdapter: ColumnAdapter<LocalDateTime, String>,
	seasonAdapter: ColumnAdapter<Season, Long>,
	specializationAdapter: ColumnAdapter<TeamPlayer.Specialization, Long>,
	durationAdapter: ColumnAdapter<Duration, Long>,
	positionAdapter: ColumnAdapter<PlayerPosition, Long>,
	matchIdAdapter: ColumnAdapter<MatchId, Long>,
	tourIdAdapter: ColumnAdapter<TourId, Long>,
): DatabaseFactory {

	private val driver: SqlDriver by lazy {
		JdbcSqliteDriver(url = appConfig.databaseConfig.jdbcUrl)
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
			),
			team_player_modelAdapter = Team_player_model.Adapter(
				image_urlAdapter = urlAdapter,
				player_idAdapter = playerIdAdapter,
				updated_atAdapter = localDateTimeAdapter,
				specializationAdapter = specializationAdapter,
			),
			match_modelAdapter = Match_model.Adapter(
				dateAdapter = zonedDateAdapter,
				idAdapter = matchIdAdapter,
				tour_idAdapter = tourIdAdapter,
			),
			point_modelAdapter = Point_model.Adapter(
				end_timeAdapter = zonedDateAdapter,
				start_timeAdapter = zonedDateAdapter,
			),
			set_modelAdapter = Set_model.Adapter(
				end_timeAdapter = zonedDateAdapter,
				start_timeAdapter = zonedDateAdapter,
				match_idAdapter = matchIdAdapter,
				durationAdapter = durationAdapter,
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
			),
			team_modelAdapter = Team_model.Adapter(
				idAdapter = teamIdAdapter,
			),
			match_appearance_modelAdapter = Match_appearance_model.Adapter(
				match_idAdapter = matchIdAdapter,
			),
			match_statistics_modelAdapter = Match_statistics_model.Adapter(
				idAdapter = matchIdAdapter,
				phaseAdapter = EnumColumnAdapter(),
				updated_atAdapter = localDateTimeAdapter,
				tour_idAdapter = tourIdAdapter,
			),
			play_attack_modelAdapter = Play_attack_model.Adapter(
				receive_effectAdapter = EnumColumnAdapter(),
				set_effectAdapter = EnumColumnAdapter(),
			),
			play_modelAdapter = Play_model.Adapter(
				effectAdapter = EnumColumnAdapter(),
				positionAdapter = positionAdapter,
			),
			play_receive_modelAdapter = Play_receive_model.Adapter(
				attack_effectAdapter = EnumColumnAdapter(),
				set_effectAdapter = EnumColumnAdapter(),
			),
			play_serve_modelAdapter = Play_serve_model.Adapter(
				receiver_effectAdapter = EnumColumnAdapter(),
			),
			play_set_modelAdapter = Play_set_model.Adapter(
				attacker_positionAdapter = positionAdapter,
				attack_effectAdapter = EnumColumnAdapter(),
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