package com.kamilh.volleyballstats.storage.stats

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayAttackQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface AttackStatsStorage {

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
            Attempts, Kill, Efficiency, Error, PointWinPercent, KillBreakPoint, EfficiencyBreakPoint, ErrorBreakPoint,
            KillSideOut, EfficiencySideOut, ErrorSideOut,
        }
    }

    data class Model(
        val specialization: Specialization,
        val teamName: String,
        val fullTeamName: String,
        val name: String,
        val attempts: Long,
        val kill: Double,
        val efficiency: Double,
        val error: Double,
        val pointWinPercent: Double,
        val killBreakPoint: Double,
        val efficiencyBreakPoint: Double,
        val errorBreakPoint: Double,
        val killSideOut: Double,
        val efficiencySideOut: Double,
        val errorSideOut: Double,
    ) : StatsModel
}

private val AttackStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        AttackStatsStorage.Request.SortBy.Attempts -> "attempts"
        AttackStatsStorage.Request.SortBy.Kill -> "kill"
        AttackStatsStorage.Request.SortBy.Efficiency -> "efficiency"
        AttackStatsStorage.Request.SortBy.Error -> "error"
        AttackStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
        AttackStatsStorage.Request.SortBy.KillBreakPoint -> "kill_break_point"
        AttackStatsStorage.Request.SortBy.EfficiencyBreakPoint -> "efficiency_break_point"
        AttackStatsStorage.Request.SortBy.ErrorBreakPoint -> "error_break_point"
        AttackStatsStorage.Request.SortBy.KillSideOut -> "kill_side_out"
        AttackStatsStorage.Request.SortBy.EfficiencySideOut -> "efficiency_side_out"
        AttackStatsStorage.Request.SortBy.ErrorSideOut -> "error_side_out"
    }

@Inject
class SqlAttackStats(
    private val playAttackQueries: PlayAttackQueries,
    private val queryRunner: QueryRunner,
): AttackStatsStorage {

    override fun getStats(request: AttackStatsStorage.Request): Flow<List<AttackStatsStorage.Model>> =
        playAttackQueries.selectAttackStats(
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
        kill: Double?,
        efficiency: Double?,
        error: Double?,
        point_win_percent: Double?,
        kill_break_point: Double?,
        efficiency_break_point: Double?,
        error_break_point: Double?,
        kill_side_out: Double?,
        efficiency_side_out: Double?,
        error_side_out: Double?,
    ) -> AttackStatsStorage.Model = {
        specialization,
        name,
        code,
        team_name,
        attempts,
        kill,
        efficiency,
        error,
        point_win_percent,
        kill_break_point,
        efficiency_break_point,
        error_break_point,
        kill_side_out,
        efficiency_side_out,
        error_side_out ->
        AttackStatsStorage.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
            fullTeamName = team_name,
            name = name ?: "",
            attempts = attempts,
            kill = kill ?: 0.0,
            efficiency = efficiency ?: 0.0,
            error = error ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
            killBreakPoint = kill_break_point ?: 0.0,
            efficiencyBreakPoint = efficiency_break_point ?: 0.0,
            errorBreakPoint = error_break_point ?: 0.0,
            killSideOut = kill_side_out ?: 0.0,
            efficiencySideOut = efficiency_side_out ?: 0.0,
            errorSideOut = error_side_out ?: 0.0,
        )
    }
}
