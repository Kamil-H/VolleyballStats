package com.kamilh.storage

import com.kamilh.Singleton
import com.kamilh.databse.*
import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.TransacterQueryRunner
import com.kamilh.storage.common.adapters.*
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Provides
import kotlin.time.Duration

interface StorageModule {

    val AppConfigDatabaseFactory.bind: DatabaseFactory
        @Provides get() = this

    @Singleton
    @Provides
    fun teamQueries(database: DatabaseFactory): TeamQueries =
        database.database.teamQueries

    @Singleton
    @Provides
    fun tourTeamQueries(database: DatabaseFactory): TourTeamQueries =
        database.database.tourTeamQueries

    @Singleton
    @Provides
    fun tourQueries(database: DatabaseFactory): TourQueries =
        database.database.tourQueries

    @Singleton
    @Provides
    fun leagueQueries(database: DatabaseFactory): LeagueQueries =
        database.database.leagueQueries

    @Singleton
    @Provides
    fun teamPlayerQueries(database: DatabaseFactory): TeamPlayerQueries =
        database.database.teamPlayerQueries

    @Singleton
    @Provides
    fun matchStatisticsQueries(database: DatabaseFactory): MatchStatisticsQueries =
        database.database.matchStatisticsQueries

    @Singleton
    @Provides
    fun playQueries(database: DatabaseFactory): PlayQueries =
        database.database.playQueries

    @Singleton
    @Provides
    fun playerQueries(database: DatabaseFactory): PlayerQueries =
        database.database.playerQueries

    @Singleton
    @Provides
    fun playAttackQueries(database: DatabaseFactory): PlayAttackQueries =
        database.database.playAttackQueries

    @Singleton
    @Provides
    fun playBlockQueries(database: DatabaseFactory): PlayBlockQueries =
        database.database.playBlockQueries

    @Singleton
    @Provides
    fun playDigQueries(database: DatabaseFactory): PlayDigQueries =
        database.database.playDigQueries

    @Singleton
    @Provides
    fun playFreeballQueries(database: DatabaseFactory): PlayFreeballQueries =
        database.database.playFreeballQueries

    @Singleton
    @Provides
    fun playReceiveQueries(database: DatabaseFactory): PlayReceiveQueries =
        database.database.playReceiveQueries

    @Singleton
    @Provides
    fun playServeQueries(database: DatabaseFactory): PlayServeQueries =
        database.database.playServeQueries

    @Singleton
    @Provides
    fun playSetQueries(database: DatabaseFactory): PlaySetQueries =
        database.database.playSetQueries

    @Singleton
    @Provides
    fun pointQueries(database: DatabaseFactory): PointQueries =
        database.database.pointQueries

    @Singleton
    @Provides
    fun pointLineupQueries(database: DatabaseFactory): PointLineupQueries =
        database.database.pointLineupQueries

    @Singleton
    @Provides
    fun setQueries(database: DatabaseFactory): SetQueries =
        database.database.setQueries

    @Singleton
    @Provides
    fun matchAppearanceQueries(database: DatabaseFactory): MatchAppearanceQueries =
        database.database.matchAppearanceQueries

    @Singleton
    @Provides
    fun matchQueries(database: DatabaseFactory): MatchQueries =
        database.database.matchQueries

    @Singleton
    @Provides
    fun queryRunner(database: DatabaseFactory, appDispatchers: AppDispatchers): QueryRunner =
        TransacterQueryRunner(
            transacter = database.database,
            appDispatchers = appDispatchers,
        )

    val ZonedDateTimeAdapter.bind: ColumnAdapter<ZonedDateTime, String>
        @Provides get() = this

    val UrlAdapter.bind: ColumnAdapter<Url, String>
        @Provides get() = this

    val TeamIdAdapter.bind: ColumnAdapter<TeamId, Long>
        @Provides get() = this

    val PlayerIdAdapter.bind: ColumnAdapter<PlayerId, Long>
        @Provides get() = this

    val CountryAdapter.bind: ColumnAdapter<Country, String>
        @Provides get() = this

    val LocalDateAdapter.bind: ColumnAdapter<LocalDate, String>
        @Provides get() = this

    val LocalDateTimeAdapter.bind: ColumnAdapter<LocalDateTime, String>
        @Provides get() = this

    val SeasonAdapter.bind: ColumnAdapter<Season, Long>
        @Provides get() = this

    val SpecializationAdapter.bind: ColumnAdapter<TeamPlayer.Specialization, Long>
        @Provides get() = this

    val MatchReportIdAdapter.bind: ColumnAdapter<MatchReportId, Long>
        @Provides get() = this

    val DurationAdapter.bind: ColumnAdapter<Duration, Long>
        @Provides get() = this

    val PositionAdapter.bind: ColumnAdapter<PlayerPosition, Long>
        @Provides get() = this

    val MatchIdAdapter.bind: ColumnAdapter<MatchId, Long>
        @Provides get() = this

    val TourIdAdapter.bind: ColumnAdapter<TourId, Long>
        @Provides get() = this

    val InMemoryAccessTokenValidator.bind: AccessTokenValidator
        @Provides get() = this

    val SqlLeagueStorage.bind: LeagueStorage
        @Provides get() = this

    val SqlMatchStatisticsStorage.bind: MatchStatisticsStorage
        @Provides get() = this

    val SqlMatchStorage.bind: MatchStorage
        @Provides get() = this

    val SqlPlayerStorage.bind: PlayerStorage
        @Provides get() = this

    val SqlTourStorage.bind: TourStorage
        @Provides get() = this

    val SqlTeamStorage.bind: TeamStorage
        @Provides get() = this
}