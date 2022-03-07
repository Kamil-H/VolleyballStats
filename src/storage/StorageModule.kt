package com.kamilh.storage

import com.kamilh.databse.*
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
import kotlin.time.Duration

private const val MODULE_NAME = "DI_STORAGE_MODULE"
val storageModule = DI.Module(name = MODULE_NAME) {
    bind<AccessTokenValidator>() with provider { InMemoryAccessTokenValidator() }

    bind<ColumnAdapter<UUID, String>>() with provider { UuidAdapter() }
    bind<ColumnAdapter<OffsetDateTime, String>>() with provider { OffsetDateAdapter() }
    bind<ColumnAdapter<Url, String>>() with provider { UrlAdapter() }
    bind<ColumnAdapter<TeamId, Long>>() with provider { TeamIdAdapter() }
    bind<ColumnAdapter<PlayerId, Long>>() with provider { PlayerIdAdapter() }
    bind<ColumnAdapter<Country, String>>() with provider { CountryAdapter() }
    bind<ColumnAdapter<LocalDate, String>>() with provider { LocalDateAdapter() }
    bind<ColumnAdapter<LocalDateTime, String>>() with provider { LocalDateTimeAdapter() }
    bind<ColumnAdapter<Season, Long>>() with provider { TourYearAdapter() }
    bind<ColumnAdapter<TeamPlayer.Specialization, Long>>() with provider { SpecializationAdapter() }
    bind<ColumnAdapter<MatchReportId, Long>>() with provider { MatchReportIdAdapter() }
    bind<ColumnAdapter<Duration, Long>>() with provider { DurationAdapter() }
    bind<ColumnAdapter<PlayerPosition, Long>>() with provider { PositionAdapter() }
    bind<ColumnAdapter<MatchId, Long>>() with provider { MatchIdAdapter() }

    bind<DatabaseFactory>() with singleton {
        AppConfigDatabaseFactory(instance(), instance(), instance(), instance(), instance(), instance(), instance(),
            instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance())
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
    bind<TourQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.tourQueries
    }
    bind<LeagueQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.leagueQueries
    }
    bind<TeamPlayerQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.teamPlayerQueries
    }
    bind<MatchStatisticsQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.matchStatisticsQueries
    }
    bind<PlayQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playQueries
    }
    bind<PlayerQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playerQueries
    }
    bind<PlayAttackQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playAttackQueries
    }
    bind<PlayBlockQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playBlockQueries
    }
    bind<PlayDigQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playDigQueries
    }
    bind<PlayFreeballQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playFreeballQueries
    }
    bind<PlayReceiveQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playReceiveQueries
    }
    bind<PlayServeQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playServeQueries
    }
    bind<PlaySetQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.playSetQueries
    }
    bind<PointQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.pointQueries
    }
    bind<PointLineupQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.pointLineupQueries
    }
    bind<SetQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.setQueries
    }
    bind<MatchAppearanceQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.matchAppearanceQueries
    }
    bind<MatchQueries>() with singleton {
        val database by di.instance<DatabaseFactory>()
        database.database.matchQueries
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

    bind<TourStorage>() with singleton {
        SqlTourStorage(instance(), instance(), instance())
    }

    bind<PlayerStorage>() with singleton {
        SqlPlayerStorage(instance(), instance(), instance(), instance(), instance())
    }

    bind<LeagueStorage>() with singleton {
        SqlLeagueStorage(instance(), instance())
    }

    bind<MatchStatisticsStorage>() with singleton {
        SqlMatchStatisticsStorage(
            instance(), instance(), instance(), instance(), instance(), instance(), instance(),
            instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(),
            instance(), instance(), instance(), instance()
        )
    }

    bind<MatchStorage>() with singleton {
        SqlMatchStorage(instance(), instance(), instance())
    }
}