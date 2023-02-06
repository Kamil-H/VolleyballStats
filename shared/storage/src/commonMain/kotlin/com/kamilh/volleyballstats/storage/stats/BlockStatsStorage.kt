package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayBlockQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface BlockStatsStorage {

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
            Attempts, Kill, KillPerAttempt, Rebound, ReboundPerAttempt, KillPlusRebound,
            KillPlusReboundPerAttempt, Error, ErrorPerAttempt, PointWinPercent,
        }
    }

    data class Model(
        val specialization: Specialization,
        val teamName: String,
        val fullTeamName: String,
        val name: String,
        val attempts: Double,
        val kill: Double,
        val killPerAttempt: Double,
        val rebound: Double,
        val reboundPerAttempt: Double,
        val killPlusRebound: Double,
        val killPlusReboundPerAttempt: Double,
        val error: Double,
        val errorPerAttempt: Double,
        val pointWinPercent: Double,
    ) : StatsModel
}

private val BlockStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        BlockStatsStorage.Request.SortBy.Attempts -> "attempts"
        BlockStatsStorage.Request.SortBy.Kill -> "kill"
        BlockStatsStorage.Request.SortBy.KillPerAttempt -> "kill_per_attempt"
        BlockStatsStorage.Request.SortBy.Rebound -> "rebound"
        BlockStatsStorage.Request.SortBy.ReboundPerAttempt -> "rebound_per_attempt"
        BlockStatsStorage.Request.SortBy.KillPlusRebound -> "kill_plus_rebound"
        BlockStatsStorage.Request.SortBy.KillPlusReboundPerAttempt -> "kill_plus_rebound_per_attempt"
        BlockStatsStorage.Request.SortBy.Error -> "error"
        BlockStatsStorage.Request.SortBy.ErrorPerAttempt -> "error_per_attempt"
        BlockStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
    }

@Inject
class SqlBlockStatsStorage(
    private val playBlockQueries: PlayBlockQueries,
    private val queryRunner: QueryRunner,
): BlockStatsStorage {

    override fun getStats(request: BlockStatsStorage.Request): Flow<List<BlockStatsStorage.Model>> =
        playBlockQueries.selectBlockStats(
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
        kill_per_attempt: Double?,
        rebound: Double?,
        rebound_per_attempt: Double?,
        kill_plus_rebound: Double?,
        kill_plus_rebound_per_attempt: Double?,
        error: Double?,
        error_per_attempt: Double?,
        point_win_percent: Double?,
    ) -> BlockStatsStorage.Model = {
            specialization: Specialization,
            name: String?,
            code: String?,
            team_name: String,
            attempts: Long,
            kill: Double?,
            kill_per_attempt: Double?,
            rebound: Double?,
            rebound_per_attempt: Double?,
            kill_plus_rebound: Double?,
            kill_plus_rebound_per_attempt: Double?,
            error: Double?,
            error_per_attempt: Double?,
            point_win_percent: Double? ->
        BlockStatsStorage.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
            fullTeamName = team_name,
            name = name ?: "",
            attempts = attempts.toDouble(),
            kill = kill ?: 0.0,
            killPerAttempt = kill_per_attempt ?: 0.0,
            rebound = rebound ?: 0.0,
            reboundPerAttempt = rebound_per_attempt ?: 0.0,
            killPlusRebound = kill_plus_rebound ?: 0.0,
            killPlusReboundPerAttempt = kill_plus_rebound_per_attempt ?: 0.0,
            error = error ?: 0.0,
            errorPerAttempt = error_per_attempt ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
        )
    }
}
