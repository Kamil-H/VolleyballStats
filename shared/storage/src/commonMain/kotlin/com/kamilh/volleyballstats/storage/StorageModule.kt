package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.ColumnAdapter
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Country
import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.PlayerPosition
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.common.TransacterQueryRunner
import com.kamilh.volleyballstats.storage.common.adapters.CountryAdapter
import com.kamilh.volleyballstats.storage.common.adapters.DurationAdapter
import com.kamilh.volleyballstats.storage.common.adapters.EffectAdapter
import com.kamilh.volleyballstats.storage.common.adapters.LocalDateAdapter
import com.kamilh.volleyballstats.storage.common.adapters.LocalDateTimeAdapter
import com.kamilh.volleyballstats.storage.common.adapters.MatchIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PhaseAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PlayerIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.PositionAdapter
import com.kamilh.volleyballstats.storage.common.adapters.SeasonAdapter
import com.kamilh.volleyballstats.storage.common.adapters.SpecializationAdapter
import com.kamilh.volleyballstats.storage.common.adapters.TeamIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.TourIdAdapter
import com.kamilh.volleyballstats.storage.common.adapters.UrlAdapter
import com.kamilh.volleyballstats.storage.common.adapters.ZonedDateTimeAdapter
import com.kamilh.volleyballstats.storage.databse.LeagueQueries
import com.kamilh.volleyballstats.storage.databse.MatchAppearanceQueries
import com.kamilh.volleyballstats.storage.databse.MatchQueries
import com.kamilh.volleyballstats.storage.databse.MatchReportQueries
import com.kamilh.volleyballstats.storage.databse.PlayAttackQueries
import com.kamilh.volleyballstats.storage.databse.PlayBlockQueries
import com.kamilh.volleyballstats.storage.databse.PlayDigQueries
import com.kamilh.volleyballstats.storage.databse.PlayFreeballQueries
import com.kamilh.volleyballstats.storage.databse.PlayQueries
import com.kamilh.volleyballstats.storage.databse.PlayReceiveQueries
import com.kamilh.volleyballstats.storage.databse.PlayServeQueries
import com.kamilh.volleyballstats.storage.databse.PlaySetQueries
import com.kamilh.volleyballstats.storage.databse.PlayerQueries
import com.kamilh.volleyballstats.storage.databse.PointLineupQueries
import com.kamilh.volleyballstats.storage.databse.PointQueries
import com.kamilh.volleyballstats.storage.databse.SetQueries
import com.kamilh.volleyballstats.storage.databse.TeamPlayerQueries
import com.kamilh.volleyballstats.storage.databse.TeamQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import com.kamilh.volleyballstats.storage.databse.TourTeamQueries
import com.kamilh.volleyballstats.storage.match.MatchSnapshotStorage
import com.kamilh.volleyballstats.storage.match.SqlMatchSnapshotStorage
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
    fun matchReportQueries(database: DatabaseFactory): MatchReportQueries =
        database.database.matchReportQueries

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

    val SpecializationAdapter.bind: ColumnAdapter<Specialization, String>
        @Provides get() = this

    val DurationAdapter.bind: ColumnAdapter<Duration, Long>
        @Provides get() = this

    val EffectAdapter.bind: ColumnAdapter<Effect, String>
        @Provides get() = this

    val PhaseAdapter.bind : ColumnAdapter<Phase, String>
        @Provides get() = this

    val PositionAdapter.bind: ColumnAdapter<PlayerPosition, Long>
        @Provides get() = this

    val MatchIdAdapter.bind: ColumnAdapter<MatchId, Long>
        @Provides get() = this

    val TourIdAdapter.bind: ColumnAdapter<TourId, Long>
        @Provides get() = this

    val SqlLeagueStorage.bind: LeagueStorage
        @Provides get() = this

    val SqlMatchReportStorage.bind: MatchReportStorage
        @Provides get() = this

    val SqlMatchStorage.bind: MatchStorage
        @Provides get() = this

    val SqlPlayerStorage.bind: PlayerStorage
        @Provides get() = this

    val SqlTourStorage.bind: TourStorage
        @Provides get() = this

    val SqlTeamStorage.bind: TeamStorage
        @Provides get() = this

    val SqlMatchSnapshotStorage.bind: MatchSnapshotStorage
        @Provides get() = this
}
