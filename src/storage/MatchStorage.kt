package com.kamilh.storage

import com.kamilh.databse.MatchQueries
import com.kamilh.databse.TourQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.OffsetDateTime

interface MatchStorage {

    suspend fun insertOrUpdate(matches: List<AllMatchesItem>, league: League, tourYear: TourYear): InsertMatchesResult

    suspend fun getAllMatches(league: League, tourYear: TourYear): Flow<List<AllMatchesItem>>
}

typealias InsertMatchesResult = Result<Unit, InsertMatchesError>

sealed class InsertMatchesError(override val message: String? = null) : Error {
    object TourNotFound : InsertMatchesError()
    class TryingToSaveSavedItems(val saved: List<AllMatchesItem.Saved>) : InsertMatchesError()
}

enum class MatchState {
    PotentiallyFinished, Scheduled, NotScheduled
}

class SqlMatchStorage(
    private val queryRunner: QueryRunner,
    private val tourQueries: TourQueries,
    private val matchQueries: MatchQueries,
): MatchStorage {

    override suspend fun insertOrUpdate(matches: List<AllMatchesItem>, league: League, tourYear: TourYear): InsertMatchesResult =
        queryRunner.runTransaction {
            val tourId = tourQueries.selectId(
                tour_year = tourYear,
                division = league.division,
                country = league.country
            ).executeAsOneOrNull() ?: return@runTransaction InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)
            val saved = matches.filterIsInstance<AllMatchesItem.Saved>()
            if (saved.isNotEmpty()) {
                return@runTransaction InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TryingToSaveSavedItems(saved))
            }
            matches.forEach { match ->
                matchQueries.insert(
                    id = match.id,
                    state = when (match) {
                        is AllMatchesItem.NotScheduled -> MatchState.NotScheduled
                        is AllMatchesItem.PotentiallyFinished -> MatchState.PotentiallyFinished
                        is AllMatchesItem.Scheduled -> MatchState.Scheduled
                        else -> error("Shouldn't try to save Saved")
                    },
                    date = (match as? AllMatchesItem.Scheduled)?.date,
                    match_statistics_id = null,
                    tour_id = tourId,
                )
            }
            InsertMatchesResult.success(Unit)
        }

    override suspend fun getAllMatches(league: League, tourYear: TourYear): Flow<List<AllMatchesItem>> =
        matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division, mapper).asFlow().mapToList()

    private val mapper: (
        id: MatchId,
        state: MatchState,
        date: LocalDateTime?,
        match_statistics_id: MatchReportId?,
        tour_id: Long,
        end_time: OffsetDateTime?,
        MAX: Long?,
        winner_team_id: TeamId?,
    ) -> AllMatchesItem = {
            id: MatchId,
            state: MatchState,
            date: LocalDateTime?,
            match_statistics_id: MatchReportId?,
            _: Long,
            end_time: OffsetDateTime?,
            _: Long?,
            winner_team_id: TeamId? ->
        if (end_time != null && winner_team_id != null && match_statistics_id != null) {
            AllMatchesItem.Saved(
                id = id,
                endTime = end_time,
                winnerId = winner_team_id,
                matchReportId = match_statistics_id,
            )
        } else {
            when (state) {
                MatchState.PotentiallyFinished -> AllMatchesItem.PotentiallyFinished(id)
                MatchState.Scheduled -> AllMatchesItem.Scheduled(id, date!!)
                MatchState.NotScheduled -> AllMatchesItem.NotScheduled(id)
            }
        }
    }
}