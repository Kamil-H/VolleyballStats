package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.Singleton
import com.kamilh.volleyballstats.databse.LeagueQueries
import com.kamilh.volleyballstats.models.Error
import com.kamilh.volleyballstats.models.League
import com.kamilh.volleyballstats.models.Result
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.common.errors.SqlError
import com.kamilh.volleyballstats.storage.common.errors.createSqlError
import me.tatarka.inject.annotations.Inject

interface LeagueStorage {

    suspend fun insert(league: League): InsertLeagueResult
}

typealias InsertLeagueResult = Result<Unit, InsertLeagueError>

enum class InsertLeagueError: Error {
    LeagueAlreadyExists;

    override val message: String = name
}

@Inject
@Singleton
class SqlLeagueStorage(
    private val queryRunner: QueryRunner,
    private val leagueQueries: LeagueQueries,
) : LeagueStorage {

    override suspend fun insert(league: League): InsertLeagueResult = try {
        queryRunner.run {
            leagueQueries.insert(
                country = league.country,
                division = league.division,
            )
        }
        Result.success(Unit)
    } catch (exception: Exception) {
        when (exception.createSqlError(tableName = "league_model", "country")) {
            SqlError.Uniqueness -> Result.failure(InsertLeagueError.LeagueAlreadyExists)
            else -> throw exception
        }
    }
}