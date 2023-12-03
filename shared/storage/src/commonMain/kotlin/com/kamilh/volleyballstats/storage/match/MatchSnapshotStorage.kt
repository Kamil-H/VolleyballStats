package com.kamilh.volleyballstats.storage.match

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.MatchQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface MatchSnapshotStorage {

    fun getMatches(season: Season): Flow<List<Model>>

    data class Model(
        val id: MatchId,
        val date: ZonedDateTime?,
        val home: Team,
        val away: Team,
        val mvpName: String?,
    ) {
        data class Team(
            val name: String,
            val logo: Url,
            val result: Int?,
        )
    }
}

@Inject
class SqlMatchSnapshotStorage(
    private val matchQueries: MatchQueries,
    private val queryRunner: QueryRunner,
): MatchSnapshotStorage {

    override fun getMatches(season: Season): Flow<List<MatchSnapshotStorage.Model>> =
        matchQueries.selectMatchSnapshotBySeason(season = season, mapper = mapper)
            .asFlow()
            .mapToList(queryRunner.dispatcher)

    @Suppress("LambdaParameterNaming")
    private val mapper: (
        match_id: MatchId,
        date: ZonedDateTime?,
        home_name: String,
        home_logo: Url,
        home_result: Long?,
        away_name: String,
        away_logo: Url,
        away_result: Long?,
        mvp_name: String?,
    ) -> MatchSnapshotStorage.Model = {
            match_id: MatchId,
            date: ZonedDateTime?,
            home_name: String,
            home_logo: Url,
            home_result: Long?,
            away_name: String,
            away_logo: Url,
            away_result: Long?,
            mvp_name: String? ->
        MatchSnapshotStorage.Model(
            id = match_id,
            date = date,
            home = MatchSnapshotStorage.Model.Team(
                name = home_name,
                logo = home_logo,
                result = home_result?.toInt(),
            ),
            away = MatchSnapshotStorage.Model.Team(
                name = away_name,
                logo = away_logo,
                result = away_result?.toInt(),
            ),
            mvpName = mvp_name,
        )
    }
}

