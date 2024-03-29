package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.*
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

interface MatchReportStorage {

    suspend fun insert(matchReport: MatchReport, tourId: TourId): InsertMatchReportResult

    fun getAllMatchReports(): Flow<List<MatchReport>>
}

typealias InsertMatchReportResult = Result<Unit, InsertMatchReportError>

sealed class InsertMatchReportError(override val message: String?) : Error {
    object TourNotFound : InsertMatchReportError("TourNotFound")
    object NoPlayersInTeams : InsertMatchReportError("NoPlayersInTeams")
    class TeamNotFound(val teamId: TeamId) : InsertMatchReportError("TeamNotFound(teamId: $teamId)")
    class PlayerNotFound(val playerIds: List<Pair<PlayerId, TeamId>>) : InsertMatchReportError(
        "PlayerNotFound(playerIds: ${playerIds.joinToString { "[${it.first}, ${it.second}]" }}"
    )
}

@Inject
@Singleton
class SqlMatchReportStorage(
    private val queryRunner: QueryRunner,
    private val teamQueries: TeamQueries,
    private val teamPlayerQueries: TeamPlayerQueries,
    private val tourTeamQueries: TourTeamQueries,
    private val matchReportQueries: MatchReportQueries,
    private val playQueries: PlayQueries,
    private val playAttackQueries: PlayAttackQueries,
    private val playBlockQueries: PlayBlockQueries,
    private val playDigQueries: PlayDigQueries,
    private val playFreeballQueries: PlayFreeballQueries,
    private val playReceiveQueries: PlayReceiveQueries,
    private val playServeQueries: PlayServeQueries,
    private val playSetQueries: PlaySetQueries,
    private val pointQueries: PointQueries,
    private val pointLineupQueries: PointLineupQueries,
    private val setQueries: SetQueries,
    private val matchAppearanceQueries: MatchAppearanceQueries,
    private val tourQueries: TourQueries,
) : MatchReportStorage {

    override suspend fun insert(matchReport: MatchReport, tourId: TourId): InsertMatchReportResult =
        queryRunner.runTransaction {
            tourQueries.selectById(tourId).executeAsOneOrNull()
                ?: return@runTransaction Result.failure<Unit, InsertMatchReportError>(
                    InsertMatchReportError.TourNotFound
                )

            val homeTeamId = tourTeamQueries.selectId(matchReport.home.teamId, tourId).executeAsOneOrNull()
                ?: return@runTransaction InsertMatchReportResult.failure(
                    InsertMatchReportError.TeamNotFound(
                        matchReport.home.teamId
                    )
                )
            teamQueries.updateCode(matchReport.home.code, matchReport.home.teamId)

            val awayTeamId = tourTeamQueries.selectId(matchReport.away.teamId, tourId).executeAsOneOrNull()
                ?: return@runTransaction InsertMatchReportResult.failure(
                    InsertMatchReportError.TeamNotFound(
                        matchReport.away.teamId
                    )
                )
            teamQueries.updateCode(matchReport.away.code, matchReport.away.teamId)

            if (matchReport.home.players.isEmpty() || matchReport.away.players.isEmpty()) {
                return@runTransaction InsertMatchReportResult.failure(InsertMatchReportError.NoPlayersInTeams)
            }

            val playerIdCache = mutableMapOf<PlayerId, Long>()
            val playersNotFound = insert(
                matchPlayers = matchReport.home.players,
                matchId = matchReport.matchId,
                tourTeamId = homeTeamId,
                teamId = matchReport.home.teamId,
            ) { playerId, id ->
                playerIdCache[playerId] = id
            } + insert(
                matchPlayers = matchReport.away.players,
                matchId = matchReport.matchId,
                tourTeamId = awayTeamId,
                teamId = matchReport.away.teamId,
            ) { playerId, id ->
                playerIdCache[playerId] = id
            }

            if (playersNotFound.isNotEmpty()) {
                return@runTransaction InsertMatchReportResult.failure(
                    InsertMatchReportError.PlayerNotFound(
                        playersNotFound
                    )
                )
            }

            matchReportQueries.insert(
                id = matchReport.matchId,
                home = homeTeamId,
                away = awayTeamId,
                mvp = playerIdCache[matchReport.mvp]!!,
                best_player = playerIdCache[matchReport.bestPlayer],
                tour_id = tourId,
                updated_at = matchReport.updatedAt,
                phase = matchReport.phase,
            )

            matchReport.sets.forEach { matchSet ->
                setQueries.insert(
                    number = matchSet.number,
                    home_score = matchSet.score.home,
                    away_score = matchSet.score.away,
                    start_time = matchSet.startTime,
                    end_time = matchSet.endTime,
                    duration = matchSet.duration,
                    match_id = matchReport.matchId,
                )
                val setId = setQueries.lastInsertRowId().executeAsOne()
                matchSet.points.forEach { matchPoint ->
                    pointQueries.insert(
                        home_score = matchPoint.score.home,
                        away_score = matchPoint.score.away,
                        start_time = matchPoint.startTime,
                        end_time = matchPoint.endTime,
                        point = when (matchPoint.point) {
                            matchReport.home.teamId -> homeTeamId
                            matchReport.away.teamId -> awayTeamId
                            else -> error("It should never happen")
                        },
                        home_lineup = pointLineupQueries.insert(matchPoint.homeLineup, playerIdCache),
                        away_lineup = pointLineupQueries.insert(matchPoint.awayLineup, playerIdCache),
                        set_id = setId,
                    )
                    val pointId = pointQueries.lastInsertRowId().executeAsOne()
                    matchPoint.playActions.forEachIndexed { index, playAction ->
                        insert(index, playAction, pointId, playerIdCache)
                    }
                }
            }
            InsertMatchReportResult.success(Unit)
        }

    private fun insert(
        matchPlayers: List<PlayerId>,
        matchId: MatchId,
        tourTeamId: Long,
        teamId: TeamId,
        onPlayerInserted: (PlayerId, Long) -> Unit,
    ): List<Pair<PlayerId, TeamId>> =
        matchPlayers.mapNotNull { matchPlayer ->
            try {
                val id = teamPlayerQueries.selectTeamPlayerByPlayerId(
                    player_id = matchPlayer,
                    tour_team_id = tourTeamId,
                ).executeAsOne()
                matchAppearanceQueries.insert(
                    tour_team_id = tourTeamId,
                    player_id = id,
                    match_id = matchId,
                )
                onPlayerInserted(matchPlayer, id)
                null
            } catch (exception: Exception) {
                matchPlayer to teamId
            }
        }

    private fun PointLineupQueries.insert(lineup: Lineup, playerIdCache: Map<PlayerId, Long>): Long {
        insert(
            p1 = playerIdCache[lineup.p1]!!,
            p2 = playerIdCache[lineup.p2]!!,
            p3 = playerIdCache[lineup.p3]!!,
            p4 = playerIdCache[lineup.p4]!!,
            p5 = playerIdCache[lineup.p5]!!,
            p6 = playerIdCache[lineup.p6]!!,
        )
        return lastInsertRowId().executeAsOne()
    }

    private fun insert(index: Int, playAction: PlayAction, pointId: Long, playerIdCache: Map<PlayerId, Long>) {
        val toId: PlayerId.() -> Long = { playerIdCache[this]!! }
        playQueries.insert(
            player_id = playerIdCache[playAction.generalInfo.playerInfo.playerId]!!,
            play_index = index,
            position = playAction.generalInfo.playerInfo.position,
            effect = playAction.generalInfo.effect,
            break_point = playAction.generalInfo.breakPoint,
            point_id = pointId,
        )
        val playId = playQueries.lastInsertRowId().executeAsOne()
        when (playAction) {
            is PlayAction.Attack -> playAttackQueries.insert(
                play_id = playId,
                side_out = playAction.sideOut,
                block_attempt = playAction.blockAttempt,
                dig_attempt = playAction.digAttempt,
                receive_effect = playAction.receiveEffect,
                receive_id = playAction.receiverId?.toId(),
                set_effect = playAction.setEffect,
                setter_id = playAction.setterId?.toId(),
            )
            is PlayAction.Block -> playBlockQueries.insert(
                play_id = playId,
                after_side_out = playAction.afterSideOut,
                attacker_id = playAction.attackerId.toId(),
                setter_id = playAction.setterId?.toId(),
            )
            is PlayAction.Dig -> playDigQueries.insert(
                play_id = playId,
                after_side_out = playAction.afterSideOut,
                attacker_id = playAction.attackerId?.toId(),
                rebounder_id = playAction.rebounderId?.toId(),
            )
            is PlayAction.Freeball -> playFreeballQueries.insert(
                play_id = playId,
                after_side_out = playAction.afterSideOut
            )
            is PlayAction.Receive -> playReceiveQueries.insert(
                play_id = playId,
                server_id = playAction.serverId.toId(),
                attack_effect = playAction.attackEffect,
                set_effect = playAction.setEffect,
            )
            is PlayAction.Serve -> playServeQueries.insert(
                play_id = playId,
                receiver_id = playAction.receiverId?.toId(),
                receiver_effect = playAction.receiveEffect,
            )
            is PlayAction.Set -> playSetQueries.insert(
                play_id = playId,
                side_out = playAction.sideOut,
                attacker_id = playAction.attackerId?.toId(),
                attacker_position = playAction.attackerPosition,
                attack_effect = playAction.attackEffect,
            )
        }
    }

    private fun <T : Any> Query<T>.mapQuery(): Flow<List<T>> = asFlow().mapToList().distinctUntilChanged()

    private fun getAllMatchReports(tourId: TourId): Flow<List<MatchReport>> {
        val stats = matchReportQueries.selectAllReportsByTourId(tourId).mapQuery()
        val matchAppearances = matchAppearanceQueries.selectAllAppearancesByTour(tourId).mapQuery()
        val sets = setQueries.selectAllBySetsTourId(tourId).mapQuery()
        val points = pointQueries.selectAllPointsByTourId(tourId).mapQuery()

        val attacks = playAttackQueries.selectAllAttacksByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val blocks = playBlockQueries.selectAllBlocksByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val digs = playDigQueries.selectAllDigsByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val freeballs =
            playFreeballQueries.selectAllFreeballsByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val serves = playServeQueries.selectAllServesByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val receives =
            playReceiveQueries.selectAllReceivesByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }
        val playSets = playSetQueries.selectAllSetsByTourId(tourId).mapQuery().map { it.map { it.toPlayAction() } }

        val playActions =
            combine(attacks, blocks, digs, freeballs, receives, serves, playSets) { list -> list.flatMap { it } }

        return combine(stats,
            matchAppearances,
            sets,
            points,
            playActions) { stats, matchAppearances, sets, points, playActions ->
            stats.map { selectAllStats ->
                MatchReport(
                    matchId = selectAllStats.id,
                    sets = sets.toMatchSet(selectAllStats.id, points, playActions),
                    home = matchAppearances.toMatchTeam(selectAllStats.home, selectAllStats.id),
                    away = matchAppearances.toMatchTeam(selectAllStats.away, selectAllStats.id),
                    mvp = selectAllStats.mvp,
                    bestPlayer = selectAllStats.best_player,
                    updatedAt = selectAllStats.updated_at,
                    phase = selectAllStats.phase,
                )
            }
        }
    }

    override fun getAllMatchReports(): Flow<List<MatchReport>> =
        tourQueries.selectAllTourIds().mapQuery().flatMapLatest { tourIds ->
            if (tourIds.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(tourIds.map { getAllMatchReports(it) }) { matches ->
                    matches.flatMap { it }
                }
            }
        }

    private fun List<SelectAllAppearancesByTour>.toMatchTeam(tourTeamId: Long, matchId: MatchId): MatchTeam =
        filter { it.tour_team_id == tourTeamId && it.match_id == matchId }
            .let {
                MatchTeam(
                    teamId = it.first().team_id,
                    code = it.first().code!!,
                    players = it.map { player -> player.player_id }
                )
            }

    private fun List<Set_model>.toMatchSet(
        matchId: MatchId,
        points: List<SelectAllPointsByTourId>,
        playActions: List<PlayActionWrapper>,
    ): List<MatchSet> =
        filter { it.match_id == matchId }
            .map { setModel ->
                MatchSet(
                    number = setModel.number,
                    score = Score(
                        home = setModel.home_score,
                        away = setModel.away_score
                    ),
                    points = points.filter { it.set_id == setModel.id }.map { pointModel ->
                        MatchPoint(
                            score = Score(
                                home = pointModel.home_score,
                                away = pointModel.away_score
                            ),
                            startTime = pointModel.start_time,
                            endTime = pointModel.end_time,
                            playActions = playActions.filter { it.pointId == pointModel.id }.map { it.playAction },
                            point = pointModel.point_team_id,
                            homeLineup = Lineup(
                                p1 = pointModel.home_p1,
                                p2 = pointModel.home_p2,
                                p3 = pointModel.home_p3,
                                p4 = pointModel.home_p4,
                                p5 = pointModel.home_p5,
                                p6 = pointModel.home_p6,
                            ),
                            awayLineup = Lineup(
                                p1 = pointModel.away_p1,
                                p2 = pointModel.away_p2,
                                p3 = pointModel.away_p3,
                                p4 = pointModel.away_p4,
                                p5 = pointModel.away_p5,
                                p6 = pointModel.away_p6,
                            ),
                        )
                    },
                    startTime = setModel.start_time,
                    endTime = setModel.end_time,
                    duration = setModel.duration,
                )
            }

    private fun SelectAllAttacksByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Attack(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                sideOut = side_out,
                blockAttempt = block_attempt,
                digAttempt = dig_attempt,
                receiveEffect = receive_effect,
                receiverId = receive_player_id,
                setEffect = set_effect,
                setterId = setter_player_id,
            )
        )

    private fun SelectAllBlocksByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Block(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                afterSideOut = after_side_out,
                attackerId = attacker_player_id,
                setterId = setter_player_id,
            )
        )

    private fun SelectAllDigsByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Dig(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                afterSideOut = after_side_out,
                attackerId = attacker_player_id,
                rebounderId = rebounder_player_id,
            )
        )

    private fun SelectAllFreeballsByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Freeball(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                afterSideOut = after_side_out,
            )
        )

    private fun SelectAllReceivesByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Receive(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                serverId = server_player_id,
                attackEffect = attack_effect,
                setEffect = set_effect,
            )
        )

    private fun SelectAllServesByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Serve(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                receiverId = receiver_player_id,
                receiveEffect = receiver_effect,
            )
        )

    private fun SelectAllSetsByTourId.toPlayAction(): PlayActionWrapper =
        PlayActionWrapper(
            pointId = point_id,
            playAction = PlayAction.Set(
                generalInfo = PlayAction.GeneralInfo(
                    playerInfo = PlayAction.PlayerInfo(
                        playerId = player_id,
                        teamId = team_id,
                        position = position,
                    ),
                    effect = effect,
                    breakPoint = break_point,
                ),
                attackerId = attacker_player_id,
                attackerPosition = attacker_position,
                sideOut = side_out,
                attackEffect = attack_effect,
            )
        )

    private class PlayActionWrapper(
        val pointId: Long,
        val playAction: PlayAction,
    )
}