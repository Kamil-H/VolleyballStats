package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.storage.databse.SelectByTourYearAndName
import com.kamilh.volleyballstats.storage.databse.TeamQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import com.kamilh.volleyballstats.storage.databse.TourTeamQueries
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.common.errors.SqlError
import com.kamilh.volleyballstats.storage.common.errors.createSqlError
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface TeamStorage {

    suspend fun insert(teams: List<Team>, tourId: TourId): InsertTeamResult

    suspend fun getAllTeams(tourId: TourId): Flow<List<Team>>

    suspend fun getTeam(name: String, code: String, tourId: TourId): Team?
}

typealias InsertTeamResult = Result<Unit, InsertTeamError>

sealed class InsertTeamError(override val message: String) : Error {
    object TourNotFound : InsertTeamError("TourNotFound")
    class TourTeamAlreadyExists(val teamIds: List<TeamId>) : InsertTeamError(
        "TourTeamAlreadyExists(teamIds: ${teamIds.joinToString { it.toString() }}"
    )
}

@Inject
@Singleton
class SqlTeamStorage(
    private val queryRunner: QueryRunner,
    private val teamQueries: TeamQueries,
    private val tourTeamQueries: TourTeamQueries,
    private val tourQueries: TourQueries,
) : TeamStorage {

    override suspend fun insert(teams: List<Team>, tourId: TourId): InsertTeamResult =
        queryRunner.runTransaction {
            val firstTeam = teams.firstOrNull() ?: return@runTransaction Result.success(Unit)
            tourQueries.selectById(tourId).executeAsOneOrNull() ?: return@runTransaction Result.failure<Unit, InsertTeamError>(InsertTeamError.TourNotFound)
            val insert: Team.() -> Exception? = {
                try {
                    teamQueries.insert(id)
                    tourTeamQueries.insert(
                        name = name,
                        image_url = teamImageUrl,
                        logo_url = logoUrl,
                        team_id = id,
                        tour_id = tourId,
                        updated_at = updatedAt,
                    )
                    null
                } catch (exception: Exception) {
                    exception
                }
            }
            val insertResult = firstTeam.insert()
            val alreadyExitsTeamIds = if (insertResult?.isTourTeamAlreadyExistsError() == true) {
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

    override suspend fun getAllTeams(tourId: TourId): Flow<List<Team>> =
        queryRunner.run {
            tourTeamQueries.selectAllByTourYear(
                tour_id = tourId,
                mapper = mapper,
            ).asFlow().mapToList(queryRunner.dispatcher)
        }

    override suspend fun getTeam(name: String, code: String, tourId: TourId): Team? =
        queryRunner.run {
            tourTeamQueries.selectByTourYearAndName(
                tour_id = tourId,
                name = name,
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
        tour_id: TourId,
        updated_at: LocalDateTime,
    ) -> Team = {
            _: Long,
            name: String,
            image_url: Url,
            logo_url: Url,
            team_id: TeamId,
            _: TourId,
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

private fun Exception.isTourTeamAlreadyExistsError(): Boolean =
    createSqlError(tableName = "tour_team_model", columnName = "team_id") is SqlError.Uniqueness