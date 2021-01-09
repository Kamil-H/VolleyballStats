package com.kamilh.storage

import com.kamilh.Database
import com.kamilh.databse.User
import com.kamilh.databse.UserQueries
import com.kamilh.models.AppDispatchers
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.TransacterQueryRunner
import com.kamilh.storage.common.adapters.UuidAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.kodein.di.*
import storage.AccessTokenValidator
import storage.InMemoryAccessTokenValidator
import storage.InMemorySubscriptionKeyStorage
import storage.SubscriptionKeyStorage
import storage.common.adapters.OffsetDateAdapter

private const val MODULE_NAME = "DI_STORAGE_MODULE"
val storageModule = DI.Module(name = MODULE_NAME) {
    bind<SubscriptionKeyStorage>() with provider { InMemorySubscriptionKeyStorage() }
    bind<AccessTokenValidator>() with provider { InMemoryAccessTokenValidator() }

    bind<Database>() with singleton {
        val uuidAdapter = UuidAdapter()
        Database(
            driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY),
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
        val appDispatchers by di.instance<AppDispatchers>()

        TransacterQueryRunner(
            transacter = database,
            appDispatchers = appDispatchers,
        )
    }

    bind<UserStorage>() with provider {
        val queryRunner by di.instance<QueryRunner>()
        val userQueries by di.instance<UserQueries>()
        SqlUserStorage(
            queryRunner = queryRunner,
            userQueries = userQueries,
        )
    }
}