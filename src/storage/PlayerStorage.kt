package com.kamilh.storage

import com.kamilh.databse.PlayerQueries
import com.kamilh.databse.TeamPlayerQueries
import com.kamilh.databse.TourQueries
import com.kamilh.databse.TourTeamQueries
import com.kamilh.models.*
import com.kamilh.storage.common.QueryRunner
import com.kamilh.storage.common.errors.SqlError
import com.kamilh.storage.common.errors.createSqlError
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import models.PlayerWithDetails
import java.time.LocalDate
import java.time.LocalDateTime

interface PlayerStorage {

    suspend fun insert(players: List<PlayerWithDetails>, league: League, tour: TourYear): InsertPlayerResult

    suspend fun getAllPlayers(teamId: TeamId, league: League, tour: TourYear): Flow<List<PlayerWithDetails>>
}

typealias InsertPlayerResult = Result<Unit, InsertPlayerError>

sealed class InsertPlayerError(override val message: String? = null) : Error {
    object TourNotFound : InsertPlayerError()
    class Errors(val teamNotFound: TeamNotFound, val teamPlayerAlreadyExists: TeamPlayerAlreadyExists) : InsertPlayerError()
    class TeamNotFound(val ids: List<TeamId>)
    class TeamPlayerAlreadyExists(val ids: List<PlayerId>)
}

class SqlPlayerStorage(
    private val queryRunner: QueryRunner,
    private val playerQueries: PlayerQueries,
    private val teamPlayerQueries: TeamPlayerQueries,
    private val tourTeamQueries: TourTeamQueries,
    private val tourQueries: TourQueries,
) : PlayerStorage {

    override suspend fun insert(players: List<PlayerWithDetails>, league: League, tour: TourYear): InsertPlayerResult =
        queryRunner.runTransaction {
            if (players.isEmpty()) {
                return@runTransaction Result.success(Unit)
            }
            val tourId = tourQueries.selectId(tour, league.division, league.country).executeAsOneOrNull() ?: return@runTransaction Result.failure<Unit, InsertPlayerError>(InsertPlayerError.TourNotFound)
            val teamIdsNotFound = mutableListOf<TeamId>()
            val playersAlreadyExists = mutableListOf<PlayerId>()
            players.groupBy { it.player.team }.forEach { (teamId, players) ->
                val tourTeamId = tourTeamQueries.selectId(teamId, tourId).executeAsOneOrNull()
                if (tourTeamId == null) {
                    teamIdsNotFound.add(teamId)
                } else {
                    players.forEach { player ->
                        val error = insertPlayer(player, tourTeamId)?.isTeamPlayerAlreadyExistsError()
                        if (error != null) {
                            playersAlreadyExists.add(player.player.id)
                        }
                    }
                }
            }
            if (teamIdsNotFound.isNotEmpty() || playersAlreadyExists.isNotEmpty()) {
                Result.failure<Unit, InsertPlayerError>(
                    InsertPlayerError.Errors(
                        teamNotFound = InsertPlayerError.TeamNotFound(teamIdsNotFound),
                        teamPlayerAlreadyExists = InsertPlayerError.TeamPlayerAlreadyExists(playersAlreadyExists)
                    )
                )
            } else {
                Result.success(Unit)
            }
        }

    private fun insertPlayer(player: PlayerWithDetails, tourTeamId: Long): Exception? =
        try {
            playerQueries.insertPlayer(
                id = player.player.id,
                name = player.player.name,
                birth_date = player.details.date,
                height = player.details.height,
                weight = player.details.weight,
                range = player.details.weight,
                updated_at = player.details.updatedAt,
            )
            teamPlayerQueries.insertPlayer(
                image_url = player.player.imageUrl,
                tour_team_id = tourTeamId,
                position = player.player.specialization,
                player_id = player.player.id,
                number = player.details.number,
                updated_at = player.player.updatedAt,
            )
            null
        } catch (exception: Exception) {
            exception
        }

    override suspend fun getAllPlayers(teamId: TeamId, league: League, tour: TourYear): Flow<List<PlayerWithDetails>> =
        queryRunner.run {
            teamPlayerQueries.selectPlayersByTeam(
                team_id = teamId,
                tour_year = tour,
                division = league.division,
                country = league.country,
                mapper = mapper,
            ).asFlow().mapToList(queryRunner.dispatcher)
        }

    private val mapper: (
        image_url: Url?,
        tour_team_id: Long,
        position: Player.Specialization,
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
            position: Player.Specialization,
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
            player = Player(
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