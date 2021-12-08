package storage

import com.kamilh.Database
import com.kamilh.databse.User
import com.kamilh.models.AppConfig
import com.kamilh.models.DatabaseConfig
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
			userAdapter = User.Adapter(
				dateAdapter = offsetDateAdapter,
				subscription_keyAdapter = uuidAdapter,
				device_idAdapter = uuidAdapter,
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