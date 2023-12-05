package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.MatchQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface MatchStorage {

    suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult

    suspend fun getAllMatches(tourId: TourId): Flow<List<Match>>

    fun getMatchIdsWithReport(tourId: TourId): Flow<List<MatchId>>

    suspend fun deleteInvalidMatches(tourId: TourId)

    suspend fun deleteAll(matchIds: List<MatchId>)
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
            if (tourQueries.selectById(tourId).executeAsOneOrNull() == null) {
                InsertMatchesResult.failure(InsertMatchesError.TourNotFound)
            } else {
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
        }

    override suspend fun getAllMatches(tourId: TourId): Flow<List<Match>> =
        matchQueries.selectAllMatchesByTour(tourId, mapper).asFlow().mapToList(queryRunner.dispatcher)

    override fun getMatchIdsWithReport(tourId: TourId): Flow<List<MatchId>> =
        matchQueries.selectMatchesWithReportByTour(tourId).asFlow().mapToList(queryRunner.dispatcher)

    override suspend fun deleteInvalidMatches(tourId: TourId) = queryRunner.runTransaction {
        val invalidMatches = matchQueries.selectWeekOldNotScheduledMatches(tourId).executeAsList()
        matchQueries.deleteById(invalidMatches.map { it.id })
    }

    override suspend fun deleteAll(matchIds: List<MatchId>) = queryRunner.run {
        matchQueries.deleteById(matchIds)
    }

    private val mapper: (
        id: MatchId, date: ZonedDateTime?, home_id: TeamId, away_id: TeamId, match_statistics_id: MatchId?,
    ) -> Match = { id: MatchId, date: ZonedDateTime?, homeId: TeamId, awayId: TeamId, matchStatisticsId: MatchId? ->
        Match(
            id = id,
            date = date,
            home = homeId,
            away = awayId,
            hasReport = matchStatisticsId != null,
        )
    }
}
