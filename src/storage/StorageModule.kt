package com.kamilh.storage

import com.kamilh.Database
import com.kamilh.authorization.CredentialsValidator
import com.kamilh.databse.User
import com.kamilh.databse.UserQueries
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.TransacterQueryRunner
import com.kamilh.storage.common.adapters.UuidAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.kodein.di.*
import org.kodein.di.ktor.di
import storage.AccessTokenValidator
import storage.InMemoryAccessTokenValidator
import storage.common.adapters.OffsetDateAdapter

private const val MODULE_NAME = "DI_STORAGE_MODULE"
val storageModule = DI.Module(name = MODULE_NAME) {
    bind<AccessTokenValidator>() with provider { InMemoryAccessTokenValidator() }

    bind<SqlDriver>() with singleton { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }

    bind<Database>() with singleton {
        val uuidAdapter = UuidAdapter()
        val driver by di.instance<SqlDriver>()
        Database(
            driver = driver,
            userAdapter = User.Adapter(
                dateAdapter = OffsetDateAdapter(),
                subscription_keyAdapter = uuidAdapter,
                device_idAdapter = uuidAdapter,
            )
        )
    }
    bind<UserQueries>() with singleton {
        val database by di.instance<Database>()
        database.userQueries
    }

    bind<QueryRunner>() with singleton {
        val database by di.instance<Database>()
        TransacterQueryRunner(
            transacter = database,
            appDispatchers = instance(),
        )
    }

    bind<UserStorage>() with provider {
        SqlUserStorage(instance(), instance())
    }
}