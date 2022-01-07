package storage

import com.kamilh.*
import com.kamilh.models.*
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

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
    intAdapter: ColumnAdapter<Int, Long>,
    localDateAdapter: ColumnAdapter<LocalDate, String>,
    localDateTimeAdapter: ColumnAdapter<LocalDateTime, String>,
    tourYearAdapter: ColumnAdapter<TourYear, Long>,
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
			),
			player_modelAdapter = Player_model.Adapter(
				idAdapter = playerIdAdapter,
				birth_dateAdapter = offsetDateAdapter,
			),
			team_player_modelAdapter = Team_player_model.Adapter(
				image_urlAdapter = urlAdapter,
				player_idAdapter = playerIdAdapter,
			),
			match_modelAdapter = Match_model.Adapter(
				dateAdapter = offsetDateAdapter,
			),
			match_report_modelAdapter = Match_report_model.Adapter(
				updatedAdapter = offsetDateAdapter,
			),
			point_modelAdapter = Point_model.Adapter(
				end_timeAdapter = offsetDateAdapter,
				start_timeAdapter = offsetDateAdapter,
			),
			set_modelAdapter = Set_model.Adapter(
				end_timeAdapter = offsetDateAdapter,
				start_timeAdapter = offsetDateAdapter,
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