package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlaySetQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface SetStatsStorage {

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
            Attempts, Perfect, PerfectPositive, Efficiency, Errors, ErrorsPercent, PointWinPercent,
        }
    }

    data class Model(
        val specialization: Specialization,
        val teamName: String,
        val fullTeamName: String,
        val name: String,
        val attempts: Double,
        val perfect: Double,
        val perfectPositive: Double,
        val efficiency: Double,
        val errors: Double,
        val errorsPercent: Double,
        val pointWinPercent: Double,
    ) : StatsModel
}

private val SetStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        SetStatsStorage.Request.SortBy.Attempts -> "attempts"
        SetStatsStorage.Request.SortBy.Perfect -> "perfect"
        SetStatsStorage.Request.SortBy.PerfectPositive -> "perfect_positive"
        SetStatsStorage.Request.SortBy.Efficiency -> "efficiency"
        SetStatsStorage.Request.SortBy.Errors -> "errors"
        SetStatsStorage.Request.SortBy.ErrorsPercent -> "errors_percent"
        SetStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
    }

@Inject
class SqlSetStatsStorage(
    private val playSetQueries: PlaySetQueries,
    private val queryRunner: QueryRunner,
): SetStatsStorage {

    override fun getStats(request: SetStatsStorage.Request): Flow<List<SetStatsStorage.Model>> =
        playSetQueries.selectSetStats(
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
        perfect: Double?,
        perfect_positive: Double?,
        efficiency: Double?,
        errors: Double?,
        errors_percent: Double?,
        point_win_percent: Double?
    ) -> SetStatsStorage.Model = {
            specialization: Specialization,
            name: String?,
            code: String?,
            team_name: String,
            attempts: Long,
            perfect: Double?,
            perfect_positive: Double?,
            efficiency: Double?,
            errors: Double?,
            errors_percent: Double?,
            point_win_percent: Double? ->
        SetStatsStorage.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
            fullTeamName = team_name,
            name = name ?: "",
            attempts = attempts.toDouble(),
            perfect = perfect ?: 0.0,
            perfectPositive = perfect_positive ?: 0.0,
            efficiency = efficiency ?: 0.0,
            errors = errors ?: 0.0,
            errorsPercent = errors_percent ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
        )
    }
}
