package com.kamilh.storage

import com.kamilh.databse.TourQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

interface TourStorage {

    suspend fun insert(tour: Tour): InsertTourResult

    suspend fun getAllByLeague(league: League): Flow<List<Tour>>

    suspend fun getByTourYearAndLeague(tourYear: TourYear, league: League): Flow<Tour?>

    suspend fun update(tour: Tour, endTime: LocalDate)
}

typealias InsertTourResult = Result<Unit, InsertTourError>

enum class InsertTourError: Error {
    LeagueNotFound, TourAlreadyExists;

    override val message: String = name
}

class SqlTourStorage(
    private val queryRunner: QueryRunner,
    private val tourQueries: TourQueries,
    private val clock: Clock,
) : TourStorage {

    override suspend fun insert(tour: Tour): InsertTourResult = try {
        queryRunner.run {
            tourQueries.insert(
                name = tour.name,
                tour_year = tour.year,
                country = tour.league.country,
                division = tour.league.division,
                start_date = tour.startDate,
                end_date = tour.endDate,
                winner_id = tour.winnerId,
                updated_at = tour.updatedAt,
            )
        }
        Result.success(Unit)
    } catch (exception: Exception) {
        when (exception.createSqlError(tableName = "tour_model", columnName = "league_id")) {
            SqlError.Uniqueness -> Result.failure(InsertTourError.TourAlreadyExists)
            SqlError.NotNull -> Result.failure(InsertTourError.LeagueNotFound)
            else -> throw exception
        }
    }

    override suspend fun getAllByLeague(league: League): Flow<List<Tour>> =
        tourQueries.selectAllByLeague(league.country, league.division, mapper).asFlow().mapToList(queryRunner.dispatcher)

    override suspend fun getByTourYearAndLeague(tourYear: TourYear, league: League): Flow<Tour?> =
        tourQueries.selectByTourAndLeague(tourYear, league.country, league.division, mapper).asFlow().mapToOneOrNull(queryRunner.dispatcher)

    override suspend fun update(tour: Tour, endTime: LocalDate) {
        queryRunner.run {
            tourQueries.updateEndTime(
                end_date = endTime,
                updated_at = LocalDateTime.now(clock),
                tour_year = tour.year,
                country = tour.league.country,
                division = tour.league.division,
            )
        }
    }

    private val mapper: (
        name: String,
        tour_year: TourYear,
        start_date: LocalDate,
        end_date: LocalDate?,
        winner_id: TeamId?,
        updated_at: LocalDateTime,
        country: Country,
        division: Int,
    ) -> Tour = {
            name: String,
            tour_year: TourYear,
            start_date: LocalDate,
            end_date: LocalDate?,
            winner_id: TeamId?,
            updated_at: LocalDateTime,
            country: Country,
            division: Int, ->
        Tour(
            name = name,
            year = tour_year,
            league = League(country, division),
            startDate = start_date,
            endDate = end_date,
            winnerId = winner_id,
            updatedAt = updated_at
        )
    }
}