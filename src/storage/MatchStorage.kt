package com.kamilh.storage

import com.kamilh.databse.MatchQueries
import com.kamilh.databse.TourQueries
import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

interface MatchStorage {

    suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult

    suspend fun getAllMatches(tourId: TourId): Flow<List<Match>>
}

typealias InsertMatchesResult = Result<Unit, InsertMatchesError>

sealed class InsertMatchesError(override val message: String) : Error {
    object TourNotFound : InsertMatchesError("TourNotFound")
}

enum class MatchState {
    PotentiallyFinished, Scheduled, NotScheduled
}

class SqlMatchStorage(
    private val queryRunner: QueryRunner,
    private val matchQueries: MatchQueries,
    private val tourQueries: TourQueries,
): MatchStorage {

    override suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult =
        queryRunner.runTransaction {
            tourQueries.selectById(tourId).executeAsOneOrNull() ?: return@runTransaction Result.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)
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