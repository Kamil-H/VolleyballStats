package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.storage.databse.MatchQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface MatchStorage {

    suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult

    suspend fun getAllMatches(tourId: TourId): Flow<List<Match>>
}

typealias InsertMatchesResult = Result<Unit, InsertMatchesError>

sealed class InsertMatchesError(override val message: String) : Error {
    object TourNotFound : InsertMatchesError("TourNotFound")
}

@Inject
@Singleton
class SqlMatchStorage(
    private val queryRunner: QueryRunner,
    private val matchQueries: MatchQueries,
    private val tourQueries: TourQueries,
): MatchStorage {

    override suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult =
        queryRunner.runTransaction {
            tourQueries.selectById(tourId).executeAsOneOrNull() ?: return@runTransaction InsertMatchesResult.failure(InsertMatchesError.TourNotFound)
            matches.forEach { match ->
                matchQueries.insert(
                    id = match.id,
                    date = match.date,
                    tour_id = tourId,
                    home_id = match.home,
                    away_id = match.away,
                    home_tour_id = tourId,
                    away_tour_id = tourId,
                )
            }
            InsertMatchesResult.success(Unit)
        }

    override suspend fun getAllMatches(tourId: TourId): Flow<List<Match>> =
        matchQueries.selectAllMatchesByTour(tourId, mapper).asFlow().mapToList()

    private val mapper: (
        id: MatchId, date: ZonedDateTime?, home_id: TeamId, away_id: TeamId, match_statistics_id: MatchId?,
    ) -> Match = { id: MatchId, date: ZonedDateTime?, home_id: TeamId, away_id: TeamId, match_statistics_id: MatchId? ->
        Match(
            id = id,
            date = date,
            home = home_id,
            away = away_id,
            hasReport = match_statistics_id != null,
        )
    }
}