package com.kamilh.volleyballstats.storage.stats

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.PlayDigQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface DigStatsStorage {

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
            Attempts, Digs, SuccessPercent, Errors, PointWinPercent
        }
    }

    data class Model(
        val specialization: Specialization,
        val teamName: String,
        val fullTeamName: String,
        val name: String,
        val attempts: Double,
        val digs: Double,
        val successPercent: Double,
        val errors: Double,
        val pointWinPercent: Double,
    ) : StatsModel
}

private val DigStatsStorage.Request.SortBy.fieldName: String
    get() = when (this) {
        DigStatsStorage.Request.SortBy.Attempts -> "attempts"
        DigStatsStorage.Request.SortBy.Digs -> "digs"
        DigStatsStorage.Request.SortBy.SuccessPercent -> "success_percent"
        DigStatsStorage.Request.SortBy.Errors -> "errors"
        DigStatsStorage.Request.SortBy.PointWinPercent -> "point_win_percent"
    }

@Inject
class SqlDigStatsStorage(
    private val playDigQueries: PlayDigQueries,
    private val queryRunner: QueryRunner,
): DigStatsStorage {

    override fun getStats(request: DigStatsStorage.Request): Flow<List<DigStatsStorage.Model>> =
        playDigQueries.selectDigStats(
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
        digs: Double?,
        success_percent: Double?,
        errors: Double?,
        point_win_percent: Double?
    ) -> DigStatsStorage.Model = {
            specialization: Specialization,
            name: String?,
            code: String?,
            team_name: String,
            attempts: Long,
            digs: Double?,
            success_percent: Double?,
            errors: Double?,
            point_win_percent: Double? ->
        DigStatsStorage.Model(
            specialization = specialization,
            teamName = code.orEmpty(),
            fullTeamName = team_name,
            name = name ?: "",
            attempts = attempts.toDouble(),
            digs = digs ?: 0.0,
            successPercent = success_percent ?: 0.0,
            errors = errors ?: 0.0,
            pointWinPercent = point_win_percent ?: 0.0,
        )
    }
}
