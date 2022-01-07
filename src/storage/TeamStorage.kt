package com.kamilh.storage

import com.kamilh.Tour_team_model
import com.kamilh.databse.TeamQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.models.Error
import com.kamilh.models.Result
import com.kamilh.models.Team
import com.kamilh.models.TourYear
import com.kamilh.storage.common.QueryRunner

interface TeamStorage {

    suspend fun insert(tour: TourYear, teams: List<Team>): InsertTeamResult

    suspend fun getAllTeams(tour: TourYear): List<Team>

    suspend fun getTeam(name: String, tour: TourYear): Team?
}

typealias InsertTeamResult = Result<Unit, InsertTeamError>

enum class InsertTeamError(override val message: String? = null): Error {
    TourNotFound
}

class SqlTeamStorage(
    private val queryRunner: QueryRunner,
    private val teamQueries: TeamQueries,
    private val tourTeamQueries: TourTeamQueries,
) : TeamStorage {

    override suspend fun insert(tour: TourYear, teams: List<Team>): InsertTeamResult =
        try {
            queryRunner.runTransaction {
                teams.forEach { team ->
                    teamQueries.insert(team.id)
                    tourTeamQueries.insertTourTeam(
                        name = team.name,
                        image_url = team.teamImageUrl,
                        logo_url = team.logoUrl,
                        team_id = team.id,
                        tour_year = tour,
                    )
                }
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(InsertTeamError.TourNotFound)
        }

    override suspend fun getAllTeams(tour: TourYear): List<Team> =
        queryRunner.run {
            tourTeamQueries.getTeam(tour).executeAsList().map(Tour_team_model::toTeam)
        }

    override suspend fun getTeam(name: String, tour: TourYear): Team? =
        queryRunner.run {
            tourTeamQueries.getTeamByName(tour, name).executeAsOneOrNull()?.toTeam()
        }
}

private fun Tour_team_model.toTeam(): Team =
    Team(
        id = team_id,
        name = name,
        teamImageUrl = image_url,
        logoUrl = logo_url,
    )

private val TourYear.name: String get() = value.toString()