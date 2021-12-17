package storage

import com.kamilh.*
import com.kamilh.models.*
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.time.OffsetDateTime
import java.util.*

interface DatabaseFactory {
	val database: Database

	fun connect()
	fun close()
}

internal class AppConfigDatabaseFactory(
	appConfig: AppConfig,
	uuidAdapter: ColumnAdapter<UUID, String>,
	offsetDateAdapter : ColumnAdapter<OffsetDateTime, String>,
	urlAdapter: ColumnAdapter<Url, String>,
	teamIdAdapter: ColumnAdapter<TeamId, Long>,
	playerIdAdapter: ColumnAdapter<PlayerId, Long>,
): DatabaseFactory {

	private val driver: SqlDriver by lazy {
		val databaseConfig: DatabaseConfig = appConfig.databaseConfig
		HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = databaseConfig.jdbcUrl
				username = databaseConfig.username
				password = databaseConfig.password
				maximumPoolSize = databaseConfig.maxPoolSize
			}
		).asJdbcDriver()
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
			),
			team_player_modelAdapter = Team_player_model.Adapter(
				image_urlAdapter = urlAdapter,
				player_idAdapter = playerIdAdapter,
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