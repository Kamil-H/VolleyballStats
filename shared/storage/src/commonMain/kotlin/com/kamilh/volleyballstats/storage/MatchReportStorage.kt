package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Lineup
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchPoint
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.models.MatchSet
import com.kamilh.volleyballstats.domain.models.MatchTeam
import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Score
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.storage.common.QueryRunner
import com.kamilh.volleyballstats.storage.databse.MatchAppearanceQueries
import com.kamilh.volleyballstats.storage.databse.MatchReportQueries
import com.kamilh.volleyballstats.storage.databse.PlayAttackQueries
import com.kamilh.volleyballstats.storage.databse.PlayBlockQueries
import com.kamilh.volleyballstats.storage.databse.PlayDigQueries
import com.kamilh.volleyballstats.storage.databse.PlayFreeballQueries
import com.kamilh.volleyballstats.storage.databse.PlayQueries
import com.kamilh.volleyballstats.storage.databse.PlayReceiveQueries
import com.kamilh.volleyballstats.storage.databse.PlayServeQueries
import com.kamilh.volleyballstats.storage.databse.PlaySetQueries
import com.kamilh.volleyballstats.storage.databse.PointLineupQueries
import com.kamilh.volleyballstats.storage.databse.PointQueries
import com.kamilh.volleyballstats.storage.databse.SelectAllAppearancesByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllAttacksByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllBlocksByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllDigsByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllFreeballsByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllPointsByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllReceivesByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllServesByMatchId
import com.kamilh.volleyballstats.storage.databse.SelectAllSetsByMatchId
import com.kamilh.volleyballstats.storage.databse.SetQueries
import com.kamilh.volleyballstats.storage.databse.TeamPlayerQueries
import com.kamilh.volleyballstats.storage.databse.TeamQueries
import com.kamilh.volleyballstats.storage.databse.TourQueries
import com.kamilh.volleyballstats.storage.databse.TourTeamQueries
import com.kamilh.volleyballstats.storage.migrations.Set_model
import me.tatarka.inject.annotations.Inject

interface MatchReportStorage {

    suspend fun insert(matchReport: MatchReport, tourId: TourId): InsertMatchReportResult

    suspend fun getMatchReport(matchId: MatchId): MatchReport?
}

typealias InsertMatchReportResult = Result<Unit, InsertMatchReportError>

sealed class InsertMatchReportError(override val message: String) : Error {
    data object TourNotFound : InsertMatchReportError("TourNotFound")
    data object NoPlayersInTeams : InsertMatchReportError("NoPlayersInTeams")
    class TeamNotFound(val teamId: TeamId) : InsertMatchReportError("TeamNotFound(teamId: $teamId)")
    class PlayerNotFound(val playerIds: List<Pair<PlayerId, TeamId>>) : InsertMatchReportError(
        "PlayerNotFound(playerIds: ${playerIds.joinToString { "[${it.first}, ${it.second}]" }}"
    )
}

@Inject
@Singleton
@Suppress("LargeClass")
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
            val homeTeamId = tourTeamQueries.selectId(matchReport.home.teamId, tourId).executeAsOneOrNull()
            val awayTeamId = tourTeamQueries.selectId(matchReport.away.teamId, tourId).executeAsOneOrNull()
            when {
                tourQueries.selectById(tourId).executeAsOneOrNull() == null ->
                    InsertMatchReportResult.failure(InsertMatchReportError.TourNotFound)
                homeTeamId == null ->
                    InsertMatchReportResult.failure(InsertMatchReportError.TeamNotFound(matchReport.home.teamId))
                awayTeamId == null ->
                    InsertMatchReportResult.failure(InsertMatchReportError.TeamNotFound(matchReport.away.teamId))
                matchReport.home.players.isEmpty() || matchReport.away.players.isEmpty() ->
                    InsertMatchReportResult.failure(InsertMatchReportError.NoPlayersInTeams)
                else -> insert(matchReport = matchReport, home = homeTeamId, away = awayTeamId, tourId = tourId)
            }
        }

    private fun insert(matchReport: MatchReport, home: Long, away: Long, tourId: TourId): InsertMatchReportResult {
        updateCodes(matchReport)
        val playerIdCache = mutableMapOf<PlayerId, Long>()
        val playersNotFound = preparePlayerCache(matchReport, playerIdCache, home = home, away = away)
        return if (playersNotFound.isNotEmpty()) {
            InsertMatchReportResult.failure(InsertMatchReportError.PlayerNotFound(playersNotFound))
        } else {
            matchReportQueries.insert(
                id = matchReport.matchId, home = home, away = away, mvp = playerIdCache[matchReport.mvp]!!,
                best_player = playerIdCache[matchReport.bestPlayer], tour_id = tourId, updated_at = matchReport.updatedAt,
                phase = matchReport.phase,
            )
            matchReport.sets.forEach { matchSet ->
                insert(matchSet = matchSet, matchId = matchReport.matchId)
                val setId = setQueries.lastInsertRowId().executeAsOne()
                matchSet.points.forEach { matchPoint ->
                    insert(matchPoint = matchPoint, matchReport = matchReport, playerIdCache = playerIdCache,
                        homeTeamId = home, awayTeamId = away, setId = setId,
                    )
                    val pointId = pointQueries.lastInsertRowId().executeAsOne()
                    matchPoint.playActions.forEachIndexed { index, playAction ->
                        insert(index, playAction, pointId, playerIdCache)
                    }
                }
            }
            InsertMatchReportResult.success(Unit)
        }
    }

    private fun updateCodes(matchReport: MatchReport) {
        teamQueries.updateCode(matchReport.home.code, matchReport.home.teamId)
        teamQueries.updateCode(matchReport.away.code, matchReport.away.teamId)
    }

    private fun insert(matchId: MatchId, matchSet: MatchSet) {
        setQueries.insert(
            number = matchSet.number,
            home_score = matchSet.score.home,
            away_score = matchSet.score.away,
            start_time = matchSet.startTime,
            end_time = matchSet.endTime,
            duration = matchSet.duration,
            match_id = matchId,
        )
    }

    private fun insert(
        matchPoint: MatchPoint,
        matchReport: MatchReport,
        playerIdCache: Map<PlayerId, Long>,
        homeTeamId: Long,
        awayTeamId: Long,
        setId: Long,
    ) {
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
    }

    private fun preparePlayerCache(
        matchReport: MatchReport,
        playerIdCache: MutableMap<PlayerId, Long>,
        home: Long,
        away: Long,
    ): List<Pair<PlayerId, TeamId>> {
        val playersNotFound = insert(
            matchPlayers = matchReport.home.players,
            matchId = matchReport.matchId,
            tourTeamId = home,
            teamId = matchReport.home.teamId,
        ) { playerId, id ->
            playerIdCache[playerId] = id
        } + insert(
            matchPlayers = matchReport.away.players,
            matchId = matchReport.matchId,
            tourTeamId = away,
            teamId = matchReport.away.teamId,
        ) { playerId, id ->
            playerIdCache[playerId] = id
        }
        return playersNotFound
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

    @Suppress("LongMethod")
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

    private fun playActionFlow(matchId: MatchId): List<PlayActionWrapper> =
        playAttackQueries.selectAllAttacksByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playBlockQueries.selectAllBlocksByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playDigQueries.selectAllDigsByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playFreeballQueries.selectAllFreeballsByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playServeQueries.selectAllServesByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playReceiveQueries.selectAllReceivesByMatchId(matchId).executeAsList().map { it.toPlayAction() } +
        playSetQueries.selectAllSetsByMatchId(matchId).executeAsList().map { it.toPlayAction() }

    private fun List<SelectAllAppearancesByMatchId>.toMatchTeam(tourTeamId: Long, matchId: MatchId): MatchTeam =
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
        points: List<SelectAllPointsByMatchId>,
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
                            homeLineup = pointModel.toHomeLineup(),
                            awayLineup = pointModel.toAwayLineup(),
                        )
                    },
                    startTime = setModel.start_time,
                    endTime = setModel.end_time,
                    duration = setModel.duration,
                )
            }

    private fun SelectAllPointsByMatchId.toHomeLineup(): Lineup =
        Lineup(
            p1 = home_p1,
            p2 = home_p2,
            p3 = home_p3,
            p4 = home_p4,
            p5 = home_p5,
            p6 = home_p6,
        )

    private fun SelectAllPointsByMatchId.toAwayLineup(): Lineup =
        Lineup(
            p1 = away_p1,
            p2 = away_p2,
            p3 = away_p3,
            p4 = away_p4,
            p5 = away_p5,
            p6 = away_p6,
        )

    private fun SelectAllAttacksByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllBlocksByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllDigsByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllFreeballsByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllReceivesByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllServesByMatchId.toPlayAction(): PlayActionWrapper =
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

    private fun SelectAllSetsByMatchId.toPlayAction(): PlayActionWrapper =
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

    override suspend fun getMatchReport(matchId: MatchId): MatchReport? = queryRunner.runTransaction {
        matchReportQueries.selectAllReportsByMatchId(matchId).executeAsOneOrNull()?.let { selectAllStats ->
            val sets = setQueries.selectAllBySetsMatchId(matchId).executeAsList().sortedBy { it.number }
            val points = pointQueries.selectAllPointsByMatchId(matchId).executeAsList().sortedBy { it.start_time }
            val matchAppearances = matchAppearanceQueries.selectAllAppearancesByMatchId(matchId).executeAsList()
            val playActions = playActionFlow(matchId)
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

    private class PlayActionWrapper(
        val pointId: Long,
        val playAction: PlayAction,
    )
}
