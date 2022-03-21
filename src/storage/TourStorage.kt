package com.kamilh.storage

import com.kamilh.databse.TourQueries
import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
import com.kamilh.utils.CurrentDate
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

interface TourStorage {

    suspend fun insert(tour: Tour): InsertTourResult

    fun getAll(): Flow<List<Tour>>

    suspend fun getAllByLeague(league: League): Flow<List<Tour>>

    suspend fun getByTourId(tourId: TourId): Flow<Tour?>

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
) : TourStorage {

    override suspend fun insert(tour: Tour): InsertTourResult = try {
        queryRunner.run {
            tourQueries.insert(
                id = tour.id,
                name = tour.name,
                season = tour.season,
                country = tour.league.country,
                division = tour.league.division,
                start_date = tour.startDate,
                end_date = tour.endDate,
                updated_at = tour.updatedAt,
            )
        }
        Result.success(Unit)
    } catch (exception: Exception) {
        val tourAlreadyExistsError = exception.createSqlError(tableName = "tour_model", columnName = "id") as? SqlError.PrimaryKey
        val leagueNotFoundError = exception.createSqlError(tableName = "tour_model", columnName = "league_id") as? SqlError.NotNull
        if (tourAlreadyExistsError != null) {
            Result.failure(InsertTourError.TourAlreadyExists)
        } else if (leagueNotFoundError != null) {
            Result.failure(InsertTourError.LeagueNotFound)
        } else {
            throw exception
        }
    }

    override fun getAll(): Flow<List<Tour>> =
        tourQueries.selectAllWithLeageue(mapper).asFlow().mapToList(queryRunner.dispatcher)

    override suspend fun getAllByLeague(league: League): Flow<List<Tour>> =
        tourQueries.selectAllByLeague(league.country, league.division, mapper).asFlow().mapToList(queryRunner.dispatcher)

    override suspend fun getByTourId(tourId: TourId): Flow<Tour?> =
        tourQueries.selectById(tourId, mapper).asFlow().mapToOneOrNull(queryRunner.dispatcher)

    override suspend fun update(tour: Tour, endTime: LocalDate) {
        queryRunner.run {
            tourQueries.updateEndTime(
                end_date = endTime,
                updated_at = CurrentDate.localDateTime,
                season = tour.season,
                country = tour.league.country,
                division = tour.league.division,
            )
        }
    }

    private val mapper: (
        id: TourId,
        name: String,
        season: Season,
        start_date: LocalDate,
        end_date: LocalDate?,
        updated_at: LocalDateTime,
        country: Country,
        division: Int,
    ) -> Tour = {
            id: TourId,
            name: String,
            season: Season,
            start_date: LocalDate,
            end_date: LocalDate?,
            updated_at: LocalDateTime,
            country: Country,
            division: Int, ->
        Tour(
            id = id,
            name = name,
            season = season,
            league = League(country, division),
            startDate = start_date,
            endDate = end_date,
            updatedAt = updated_at
        )
    }
}