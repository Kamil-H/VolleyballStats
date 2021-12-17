package com.kamilh.storage

import com.kamilh.databse.TeamQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.Tour_team_model
import com.kamilh.models.Team
import com.kamilh.models.Tour
import com.kamilh.storage.common.QueryRunner

interface TeamStorage {

    suspend fun insert(tour: Tour, teams: List<Team>)

    suspend fun getAllTeams(tour: Tour): List<Team>

    suspend fun getTeam(name: String, tour: Tour): Team?
}

class SqlTeamStorage(
    private val queryRunner: QueryRunner,
    private val teamQueries: TeamQueries,
    private val tourTeamQueries: TourTeamQueries,
) : TeamStorage {

    override suspend fun insert(tour: Tour, teams: List<Team>) {
        queryRunner.runTransaction {
            teams.forEach { team ->
                teamQueries.insertTeam(team.id.value)
                tourTeamQueries.insertTourTeam(
                    name = team.name,
                    image_url = team.teamImageUrl,
                    logo_url = team.logoUrl,
                    team_id = team.id,
                    tour_name = tour.name,
                )
            }
        }
    }

    override suspend fun getAllTeams(tour: Tour): List<Team> =
        queryRunner.run {
            tourTeamQueries.getTeam(tour.name).executeAsList().map(Tour_team_model::toTeam)
        }

    override suspend fun getTeam(name: String, tour: Tour): Team? =
        queryRunner.run {
            tourTeamQueries.getTeamByName(tour.name, name).executeAsOneOrNull()?.toTeam()
        }
}

private fun Tour_team_model.toTeam(): Team =
    Team(
        id = team_id,
        name = name,
        teamImageUrl = image_url,
        logoUrl = logo_url,
    )

private val Tour.name: String get() = value.toString()