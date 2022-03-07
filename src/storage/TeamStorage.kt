package com.kamilh.storage

import com.kamilh.databse.SelectByTourYearAndName
import com.kamilh.databse.TeamQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TeamStorage {

    suspend fun insert(teams: List<Team>, league: League, season: Season): InsertTeamResult

    suspend fun getAllTeams(league: League, season: Season): Flow<List<Team>>

    suspend fun getTeam(name: String, code: String, league: League, season: Season): Team?
}

typealias InsertTeamResult = Result<Unit, InsertTeamError>

sealed class InsertTeamError(override val message: String) : Error {
    object TourNotFound : InsertTeamError("TourNotFound")
    class TourTeamAlreadyExists(val teamIds: List<TeamId>) : InsertTeamError(
        "TourTeamAlreadyExists(teamIds: ${teamIds.joinToString { it.toString() }}"
    )
}

class SqlTeamStorage(
    private val queryRunner: QueryRunner,
    private val teamQueries: TeamQueries,
    private val tourTeamQueries: TourTeamQueries,
) : TeamStorage {

    override suspend fun insert(teams: List<Team>, league: League, season: Season): InsertTeamResult =
        queryRunner.runTransaction {
            val firstTeam = teams.firstOrNull() ?: return@runTransaction Result.success(Unit)
            val insert: Team.() -> Exception? = {
                try {
                    teamQueries.insert(id)
                    tourTeamQueries.insert(
                        name = name,
                        image_url = teamImageUrl,
                        logo_url = logoUrl,
                        team_id = id,
                        season = season,
                        updated_at = updatedAt,
                        country = league.country,
                        division = league.division,
                    )
                    null
                } catch (exception: Exception) {
                    exception
                }
            }
            val insertResult = firstTeam.insert()
            val alreadyExitsTeamIds = if (insertResult?.isTourNotFoundError() == true) {
                return@runTransaction Result.failure<Unit, InsertTeamError>(InsertTeamError.TourNotFound)
            } else if (insertResult?.isTourTeamAlreadyExistsError() == true) {
                listOf(firstTeam.id)
            } else {
                emptyList()
            } + teams.drop(1).mapNotNull { team ->
                val result = team.insert()
                when {
                    result == null -> null
                    result.isTourTeamAlreadyExistsError() -> team.id
                    else -> throw result
                }
            }
            if (alreadyExitsTeamIds.isEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(InsertTeamError.TourTeamAlreadyExists(alreadyExitsTeamIds))
            }
        }

    override suspend fun getAllTeams(league: League, season: Season): Flow<List<Team>> =
        queryRunner.run {
            tourTeamQueries.selectAllByTourYear(
                season = season,
                division = league.division,
                country = league.country,
                mapper = mapper,
            ).asFlow().mapToList(queryRunner.dispatcher)
        }

    override suspend fun getTeam(name: String, code: String, league: League, season: Season): Team? =
        queryRunner.run {
            tourTeamQueries.selectByTourYearAndName(
                season = season,
                name = name,
                division = league.division,
                country = league.country,
                code = code,
            ).executeAsOneOrNull()?.toTeam()
        }

    private fun SelectByTourYearAndName.toTeam(): Team =
        Team(
            id = team_id,
            name = name,
            teamImageUrl = image_url,
            logoUrl = logo_url,
            updatedAt = updated_at,
        )

    private val mapper: (
        id: Long,
        name: String,
        image_url: Url,
        logo_url: Url,
        team_id: TeamId,
        tour_id: Long,
        updated_at: LocalDateTime,
    ) -> Team = {
            _: Long,
            name: String,
            image_url: Url,
            logo_url: Url,
            team_id: TeamId,
            _: Long,
            updated_at: LocalDateTime,
        ->
        Team(
            id = team_id,
            name = name,
            teamImageUrl = image_url,
            logoUrl = logo_url,
            updatedAt = updated_at,
        )
    }
}

private fun Exception.isTourNotFoundError(): Boolean =
    createSqlError(tableName = "tour_team_model", columnName = "tour_id") is SqlError.NotNull

private fun Exception.isTourTeamAlreadyExistsError(): Boolean =
    createSqlError(tableName = "tour_team_model", columnName = "team_id") is SqlError.Uniqueness