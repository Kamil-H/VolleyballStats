package com.kamilh.storage

import com.kamilh.databse.TeamQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.databse.UserQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.TransacterQueryRunner
import com.kamilh.storage.common.adapters.*
import com.squareup.sqldelight.ColumnAdapter
import org.kodein.di.*
import storage.AccessTokenValidator
import storage.AppConfigDatabaseFactory
import storage.DatabaseFactory
import storage.InMemoryAccessTokenValidator
import storage.common.adapters.OffsetDateAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

private const val MODULE_NAME = "DI_STORAGE_MODULE"
val storageModule = DI.Module(name = MODULE_NAME) {
    bind<AccessTokenValidator>() with provider { InMemoryAccessTokenValidator() }

    bind<ColumnAdapter<UUID, String>>() with provider { UuidAdapter() }
    bind<ColumnAdapter<OffsetDateTime, String>>() with provider { OffsetDateAdapter() }
    bind<ColumnAdapter<Url, String>>() with provider { UrlAdapter() }
    bind<ColumnAdapter<TeamId, Long>>() with provider { TeamIdAdapter() }
    bind<ColumnAdapter<PlayerId, Long>>() with provider { PlayerIdAdapter() }
    bind<ColumnAdapter<Country, String>>() with provider { CountryAdapter() }
    bind<ColumnAdapter<Int, Long>>() with provider { IntAdapter() }
    bind<ColumnAdapter<LocalDate, String>>() with provider { LocalDateAdapter() }
    bind<ColumnAdapter<LocalDateTime, String>>() with provider { LocalDateTimeAdapter() }
    bind<ColumnAdapter<TourYear, Long>>() with provider { TourYearAdapter() }
    bind<ColumnAdapter<Player.Specialization, Long>>() with provider { SpecializationAdapter() }

    bind<DatabaseFactory>() with singleton {
        AppConfigDatabaseFactory(instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance())
    }

    bind<UserQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.userQueries
    }

    bind<TeamQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.teamQueries
    }

    bind<TourTeamQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.tourTeamQueries
    }

    bind<QueryRunner>() with singleton {
        val database by di.instance<DatabaseFactory>()
        TransacterQueryRunner(
            transacter = database.database,
            appDispatchers = instance(),
        )
    }

    bind<UserStorage>() with provider {
        SqlUserStorage(instance(), instance())
    }

    bind<TeamStorage>() with singleton {
        SqlTeamStorage(instance(), instance(), instance())
    }
}