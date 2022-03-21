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
    class TryingToInsertFinishedItems(val finished: List<Match.Finished>) : InsertMatchesError(
        "TryingToInsertFinishedItems(finished=${finished.joinToString { it.id.toString() }})"
    )
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
            val finished = matches.filterIsInstance<Match.Finished>()
            if (finished.isNotEmpty()) {
                return@runTransaction InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TryingToInsertFinishedItems(finished))
            }
            matches.forEach { match ->
                val state = when (match) {
                    is Match.NotScheduled -> MatchState.NotScheduled
                    is Match.PotentiallyFinished -> MatchState.PotentiallyFinished
                    is Match.Scheduled -> MatchState.Scheduled
                    else -> error("Shouldn't try to save Saved")
                }
                matchQueries.insert(
                    id = match.id,
                    state = state,
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
        id: MatchId,
        date: ZonedDateTime?,
        state: MatchState,
        home_id: TeamId?,
        away_id: TeamId?,
        end_time: ZonedDateTime?,
        winner_team_id: TeamId?
    ) -> Match = {
            id: MatchId,
            date: ZonedDateTime?,
            state: MatchState,
            home_id: TeamId?,
            away_id: TeamId?,
            end_time: ZonedDateTime?,
            winner_team_id: TeamId? ->
        if (end_time != null && winner_team_id != null) {
            Match.Finished(
                id = id,
                endTime = end_time,
                winnerId = winner_team_id,
                away = away_id!!,
                home = home_id!!,
                date = date!!,
            )
        } else {
            when (state) {
                MatchState.PotentiallyFinished -> Match.PotentiallyFinished(
                    id = id, away = away_id!!, home = home_id!!, date = date!!,
                )
                MatchState.Scheduled -> Match.Scheduled(
                    id = id, away = away_id!!, home = home_id!!, date = date!!,
                )
                MatchState.NotScheduled -> Match.NotScheduled(
                    id = id, away = away_id!!, home = home_id!!, date = date,
                )
            }
        }
    }
}