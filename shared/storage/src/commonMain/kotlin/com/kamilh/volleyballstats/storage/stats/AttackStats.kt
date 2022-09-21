package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayAttackQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface AttackStats {

    fun getAttackStats(request: Request): Flow<List<Model>>

    data class Request(
        override val groupBy: StatsRequest.GroupBy,
        override val tourId: TourId,
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
    )
}

private val AttackStats.Request.SortBy.fieldName: String
    get() = when (this) {
        AttackStats.Request.SortBy.Attempts -> "attempts"
        AttackStats.Request.SortBy.Kill -> "kill"
        AttackStats.Request.SortBy.Efficiency -> "efficiency"
        AttackStats.Request.SortBy.Error -> "error"
        AttackStats.Request.SortBy.PointWinPercent -> "point_win_percent"
        AttackStats.Request.SortBy.KillBreakPoint -> "kill_break_point"
        AttackStats.Request.SortBy.EfficiencyBreakPoint -> "efficiency_break_point"
        AttackStats.Request.SortBy.ErrorBreakPoint -> "error_break_point"
        AttackStats.Request.SortBy.KillSideOut -> "kill_side_out"
        AttackStats.Request.SortBy.EfficiencySideOut -> "efficiency_side_out"
        AttackStats.Request.SortBy.ErrorSideOut -> "error_side_out"
    }

@Inject
class SqlAttackStats(
    private val playAttackQueries: PlayAttackQueries,
    private val queryRunner: QueryRunner,
): AttackStats {

    override fun getAttackStats(request: AttackStats.Request): Flow<List<AttackStats.Model>> =
        playAttackQueries.selectAttackStats(
            tour_id = request.tourId,
            group_by = request.groupBy.fieldName,
            sort_type = request.sortBy.fieldName,
            mapper = mapper,
        ).asFlow().mapToList(queryRunner.dispatcher)

    private val mapper: (
        specialization: Specialization,
        team_name: String,
        name: String?,
        image_url: Url?,
        code: String?,
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
    ) -> AttackStats.Model = {
        specialization,
        _,
        name,
        _,
        code,
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
        AttackStats.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
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
