package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayServeQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface ServeStatsStorage {

    fun getStats(request: Request): Flow<List<Model>>

    data class Request(
        override val groupBy: StatsRequest.GroupBy,
        override val seasons: List<Season>,
        override val specializations: List<Specialization>,
        override val teams: List<TeamId>,
        override val minAttempts: Long,
        val sortBy: SortBy,
    ): StatsRequest {

        enum class SortBy {
            Attempts, Efficiency, Ace, AcePercent, Freeball, FreeballPercent, AceFreeball,
            AceFreeballPercent, Errors, ErrorsPercent, PointWinPercent,
        }
    }

    data class Model(
        val specialization: Specialization,
        val teamName: String,
        val fullTeamName: String,
        val name: String,
        val attempts: Double,
        val efficiency: Double,
        val ace: Double,
        val acePercent: Double,
        val freeball: Double,
        val freeballPercent: Double,
        val aceFreeball: Double,
        val aceFreeballPercent: Double,
        val errors: Double,
        val errorsPercent: Double,
        val pointWinPercent: Double,
    ) : StatsModel
}

private val ServeStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        ServeStatsStorage.Request.SortBy.Attempts -> "attempts"
        ServeStatsStorage.Request.SortBy.Efficiency -> "efficiency"
        ServeStatsStorage.Request.SortBy.Ace -> "ace"
        ServeStatsStorage.Request.SortBy.AcePercent -> "ace_percent"
        ServeStatsStorage.Request.SortBy.Freeball -> "freeball"
        ServeStatsStorage.Request.SortBy.FreeballPercent -> "freeball_percent"
        ServeStatsStorage.Request.SortBy.AceFreeball -> "ace_freeball"
        ServeStatsStorage.Request.SortBy.AceFreeballPercent -> "ace_freeball_percent"
        ServeStatsStorage.Request.SortBy.Errors -> "errors"
        ServeStatsStorage.Request.SortBy.ErrorsPercent -> "errors_percent"
        ServeStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
    }

@Inject
class SqlServeStatsStorage(
    private val playServeQueries: PlayServeQueries,
    private val queryRunner: QueryRunner,
): ServeStatsStorage {

    override fun getStats(request: ServeStatsStorage.Request): Flow<List<ServeStatsStorage.Model>> =
        playServeQueries.selectServeStats(
            seasons = request.seasons,
            seasonsIsEmpty = request.seasons.isEmpty(),
            specializations = request.specializations,
            specializationsIsEmpty = request.specializations.isEmpty(),
            teamIds = request.teams,
            teamIdsIsEmpty = request.teams.isEmpty(),
            group_by = request.groupBy.fieldName,
            sort_type = request.sortBy.fieldName,
            min_attempts = request.minAttempts,
            mapper = mapper,
        ).asFlow().mapToList(queryRunner.dispatcher)

    @Suppress("LambdaParameterNaming")
    private val mapper: (
        specialization: Specialization,
        name: String?,
        code: String?,
        team_name: String,
        attempts: Long,
        efficiency: Double?,
        ace: Double?,
        ace_percent: Double?,
        freeball: Double?,
        freeball_percent: Double?,
        ace_freeball: Double?,
        ace_freeball_percent: Double?,
        errors: Double?,
        errors_percent: Double?,
        point_win_percent: Double?
    ) -> ServeStatsStorage.Model = {
            specialization: Specialization,
            name: String?,
            code: String?,
            team_name: String,
            attempts: Long,
            efficiency: Double?,
            ace: Double?,
            ace_percent: Double?,
            freeball: Double?,
            freeball_percent: Double?,
            ace_freeball: Double?,
            ace_freeball_percent: Double?,
            errors: Double?,
            errors_percent: Double?,
            point_win_percent: Double? ->
        ServeStatsStorage.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
            fullTeamName = team_name,
            name = name ?: "",
            attempts = attempts.toDouble(),
            efficiency = efficiency ?: 0.0,
            ace = ace ?: 0.0,
            acePercent = ace_percent ?: 0.0,
            freeball = freeball ?: 0.0,
            freeballPercent = freeball_percent ?: 0.0,
            aceFreeball = ace_freeball ?: 0.0,
            aceFreeballPercent = ace_freeball_percent ?: 0.0,
            errors = errors ?: 0.0,
            errorsPercent = errors_percent ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
        )
    }
}
