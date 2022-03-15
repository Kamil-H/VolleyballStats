package com.kamilh.storage

import com.kamilh.databse.PlayerQueries
import com.kamilh.databse.TeamPlayerQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import models.PlayerWithDetails
import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime

interface PlayerStorage {

    suspend fun insert(players: List<PlayerWithDetails>, tourId: TourId): InsertPlayerResult

    suspend fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<PlayerWithDetails>>

    suspend fun getAllPlayers(tourId: TourId): Flow<List<PlayerWithDetails>>
}

typealias InsertPlayerResult = Result<Unit, InsertPlayerError>

sealed class InsertPlayerError(override val message: String) : Error {
    object TourNotFound : InsertPlayerError("TourNotFound")
    class Errors(val teamsNotFound: List<TeamId>, val teamPlayersAlreadyExists: List<PlayerId>) : InsertPlayerError(
        "Errors(teamsNotFound: ${teamsNotFound.joinToString { it.toString() }}, teamPlayersAlreadyExists: ${teamPlayersAlreadyExists.joinToString { it.toString() }})"
    )
}

class SqlPlayerStorage(
    private val queryRunner: QueryRunner,
    private val playerQueries: PlayerQueries,
    private val teamPlayerQueries: TeamPlayerQueries,
    private val tourTeamQueries: TourTeamQueries,
) : PlayerStorage {

    override suspend fun insert(players: List<PlayerWithDetails>, tourId: TourId): InsertPlayerResult =
        queryRunner.runTransaction {
            if (players.isEmpty()) {
                return@runTransaction Result.success(Unit)
            }

            val teamIdsNotFound = mutableListOf<TeamId>()
            val playersAlreadyExists = mutableListOf<PlayerId>()
            players.groupBy { it.teamPlayer.team }.forEach { (teamId, players) ->
                val tourTeamId = tourTeamQueries.selectId(teamId, tourId).executeAsOneOrNull()
                if (tourTeamId == null) {
                    teamIdsNotFound.add(teamId)
                } else {
                    players.forEach { player ->
                        val error = insertPlayer(player, tourTeamId)?.isTeamPlayerAlreadyExistsError()
                        if (error != null) {
                            playersAlreadyExists.add(player.teamPlayer.id)
                        }
                    }
                }
            }
            if (teamIdsNotFound.isNotEmpty() || playersAlreadyExists.isNotEmpty()) {
                Result.failure<Unit, InsertPlayerError>(
                    InsertPlayerError.Errors(
                        teamsNotFound = teamIdsNotFound,
                        teamPlayersAlreadyExists = playersAlreadyExists,
                    )
                )
            } else {
                Result.success(Unit)
            }
        }

    private fun insertPlayer(player: PlayerWithDetails, tourTeamId: Long): Exception? =
        try {
            playerQueries.insertPlayer(
                id = player.teamPlayer.id,
                name = player.teamPlayer.name,
                birth_date = player.details.date,
                height = player.details.height,
                weight = player.details.weight,
                range = player.details.range,
                updated_at = player.details.updatedAt,
            )
            teamPlayerQueries.insertPlayer(
                image_url = player.teamPlayer.imageUrl,
                tour_team_id = tourTeamId,
                specialization = player.teamPlayer.specialization,
                player_id = player.teamPlayer.id,
                number = player.details.number,
                updated_at = player.teamPlayer.updatedAt,
            )
            null
        } catch (exception: Exception) {
            exception
        }

    override suspend fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<PlayerWithDetails>> =
        queryRunner.run {
            teamPlayerQueries.selectPlayersByTeam(
                team_id = teamId,
                tour_id = tourId,
                mapper = mapper,
            ).asFlow().mapToList(queryRunner.dispatcher)
        }

    override suspend fun getAllPlayers(tourId: TourId): Flow<List<PlayerWithDetails>> =
        queryRunner.run {
            teamPlayerQueries.selectPlayers(
                tour_id = tourId,
                mapper = mapper,
            ).asFlow().mapToList(queryRunner.dispatcher)
        }

    private val mapper: (
        image_url: Url?,
        tour_team_id: Long,
        position: TeamPlayer.Specialization,
        player_id: PlayerId,
        number: Int,
        name: String,
        updated_at: LocalDateTime,
        updated_at_: LocalDateTime,
        birth_date: LocalDate,
        height: Int?,
        weight: Int?,
        range: Int?,
        team_id: TeamId
    ) -> PlayerWithDetails = {
            image_url: Url?,
            _: Long,
            position: TeamPlayer.Specialization,
            player_id: PlayerId,
            number: Int,
            name: String,
            updated_at: LocalDateTime,
            updated_at_: LocalDateTime,
            birth_date: LocalDate,
            height: Int?,
            weight: Int?,
            range: Int?,
            team_id: TeamId ->
        PlayerWithDetails(
            teamPlayer = TeamPlayer(
                id = player_id,
                name = name,
                imageUrl = image_url,
                team = team_id,
                specialization = position,
                updatedAt = updated_at,
            ),
            details = PlayerDetails(
                date = birth_date,
                height = height,
                weight = weight,
                range = range,
                number = number,
                updatedAt = updated_at_,
            )
        )
    }
}

private fun Exception.isTeamPlayerAlreadyExistsError(): Boolean =
    createSqlError(tableName = "team_player_model", columnName = "player_id") is SqlError.Uniqueness