package com.kamilh.storage

import com.kamilh.Singleton
import com.kamilh.databse.LeagueQueries
import com.kamilh.models.Error
import com.kamilh.models.League
import com.kamilh.models.Result
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
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