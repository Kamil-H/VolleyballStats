package com.kamilh.volleyballstats.storage.stats

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayReceiveQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface ReceiveStatsStorage {

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
            Attempts, Perfect, PerfectPositive, Efficiency, Errors, ErrorsPercent, SideOut, PointWinPercent;
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
        val sideOut: Double,
        val pointWinPercent: Double,
    ) : StatsModel
}

private val ReceiveStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        ReceiveStatsStorage.Request.SortBy.Attempts -> "attempts"
        ReceiveStatsStorage.Request.SortBy.Perfect -> "perfect"
        ReceiveStatsStorage.Request.SortBy.PerfectPositive -> "perfect_positive"
        ReceiveStatsStorage.Request.SortBy.Efficiency -> "efficiency"
        ReceiveStatsStorage.Request.SortBy.Errors -> "errors"
        ReceiveStatsStorage.Request.SortBy.ErrorsPercent -> "errors_percent"
        ReceiveStatsStorage.Request.SortBy.SideOut -> "side_out"
        ReceiveStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
    }

@Inject
class SqlReceiveStatsStorage(
    private val playReceiveQueries: PlayReceiveQueries,
    private val queryRunner: QueryRunner,
): ReceiveStatsStorage {

    override fun getStats(request: ReceiveStatsStorage.Request): Flow<List<ReceiveStatsStorage.Model>> =
        playReceiveQueries.selectReceiveStats(
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
        side_out: Double?,
        point_win_percent: Double?
    ) -> ReceiveStatsStorage.Model = {
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
            side_out: Double?,
            point_win_percent: Double? ->
        ReceiveStatsStorage.Model(
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
            sideOut = side_out ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
        )
    }
}
