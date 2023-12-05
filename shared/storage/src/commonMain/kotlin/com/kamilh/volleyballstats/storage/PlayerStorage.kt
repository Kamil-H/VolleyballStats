package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Player
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.common.errors.SqlError
import com.kamilh.volleyballstats.storage.common.errors.createSqlError
import com.kamilh.volleyballstats.storage.databse.PlayerQueries
import com.kamilh.volleyballstats.storage.databse.TeamPlayerQueries
import com.kamilh.volleyballstats.storage.databse.TourTeamQueries
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface PlayerStorage {

    suspend fun insert(players: List<Player>, tourId: TourId): InsertPlayerResult

    fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<Player>>

    fun getAllPlayers(tourId: TourId): Flow<List<Player>>
}

typealias InsertPlayerResult = Result<Unit, InsertPlayerError>

sealed class InsertPlayerError(override val message: String) : Error {
    data object TourNotFound : InsertPlayerError("TourNotFound")
    class Errors(val teamsNotFound: List<TeamId>, val teamPlayersAlreadyExists: List<PlayerId>) : InsertPlayerError(
        "Errors(teamsNotFound: ${teamsNotFound.joinToString { it.toString() }}, teamPlayersAlreadyExists: ${teamPlayersAlreadyExists.joinToString { it.toString() }})"
    )
}

@Inject
@Singleton
class SqlPlayerStorage(
    private val queryRunner: QueryRunner,
    private val playerQueries: PlayerQueries,
    private val teamPlayerQueries: TeamPlayerQueries,
    private val tourTeamQueries: TourTeamQueries,
) : PlayerStorage {

    override suspend fun insert(players: List<Player>, tourId: TourId): InsertPlayerResult =
        queryRunner.runTransaction {
            if (players.isEmpty()) {
                Result.success(Unit)
            } else {
                insertInternal(players, tourId)
            }
        }

    private fun insertInternal(players: List<Player>, tourId: TourId): InsertPlayerResult {
        val teamIdsNotFound = mutableListOf<TeamId>()
        val playersAlreadyExists = mutableListOf<PlayerId>()
        players.groupBy { it.team }.forEach { (teamId, players) ->
            val tourTeamId = tourTeamQueries.selectId(teamId, tourId).executeAsOneOrNull()
            if (tourTeamId == null) {
                teamIdsNotFound.add(teamId)
            } else {
                playersAlreadyExists.addAll(insertPlayers(players, tourTeamId))
            }
        }
        return if (teamIdsNotFound.isNotEmpty() || playersAlreadyExists.isNotEmpty()) {
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

    private fun insertPlayers(players: List<Player>, tourTeamId: Long): List<PlayerId> =
        players.mapNotNull { player ->
            if (insertPlayer(player, tourTeamId)?.isTeamPlayerAlreadyExistsError() != null) {
                player.id
            } else {
                null
            }
        }

    private fun insertPlayer(player: Player, tourTeamId: Long): Exception? =
        try {
            playerQueries.insertPlayer(
                id = player.id,
                name = player.name,
                birth_date = player.date,
                height = player.height,
                weight = player.weight,
                range = player.range,
                updated_at = player.updatedAt,
            )
            teamPlayerQueries.insertPlayer(
                image_url = player.imageUrl,
                tour_team_id = tourTeamId,
                specialization = player.specialization,
                player_id = player.id,
                number = player.number,
                updated_at = player.updatedAt,
            )
            null
        } catch (exception: Exception) {
            exception
        }

    override fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<Player>> =
        teamPlayerQueries.selectPlayersByTeam(
            team_id = teamId,
            tour_id = tourId,
            mapper = mapper,
        ).asFlow().mapToList(queryRunner.dispatcher)

    override fun getAllPlayers(tourId: TourId): Flow<List<Player>> =
        teamPlayerQueries.selectPlayers(
            tour_id = tourId,
            mapper = mapper,
        ).asFlow().mapToList(queryRunner.dispatcher)

    private val mapper: (
        image_url: Url?,
        tour_team_id: Long,
        position: Specialization,
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
    ) -> Player = {
            imageUrl: Url?,
            _: Long,
            position: Specialization,
            playerId: PlayerId,
            number: Int,
            name: String,
            updatedAt: LocalDateTime,
            _: LocalDateTime,
            birthDate: LocalDate,
            height: Int?,
            weight: Int?,
            range: Int?,
            teamId: TeamId
        ->
        Player(
            id = playerId,
            name = name,
            imageUrl = imageUrl,
            team = teamId,
            specialization = position,
            date = birthDate,
            height = height,
            weight = weight,
            range = range,
            number = number,
            updatedAt = updatedAt,
        )
    }
}

private fun Exception.isTeamPlayerAlreadyExistsError(): Boolean =
    createSqlError(tableName = "team_player_model", columnName = "player_id") is SqlError.Uniqueness
