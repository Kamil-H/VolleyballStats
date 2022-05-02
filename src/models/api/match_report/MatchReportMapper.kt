package com.kamilh.models.api.match_report

import com.kamilh.models.*
import com.kamilh.models.api.ResponseMapper
import me.tatarka.inject.annotations.Inject

@Inject
class MatchReportMapper : ResponseMapper<MatchStatistics, MatchReportResponse> {

    override fun to(from: MatchStatistics): MatchReportResponse =
        MatchReportResponse(
            matchId = from.matchId,
            sets = from.sets.map { it.toResponse() },
            home = from.home.toResponse(),
            away = from.away.toResponse(),
            mvp = from.mvp,
            bestPlayer = from.bestPlayer,
            updatedAt = from.updatedAt,
            phase = from.phase,
        )

    private fun MatchSet.toResponse(): MatchSetResponse =
        MatchSetResponse(
            number = number,
            score = score.toResponse(),
            points = points.map { it.toResponse() },
            startTime = startTime,
            endTime = endTime,
            duration = duration,
        )

    private fun MatchPoint.toResponse(): MatchPointResponse =
        MatchPointResponse(
            score = score.toResponse(),
            startTime = startTime,
            endTime = endTime,
            playActions = playActions.map { it.toResponse() },
            point = point,
            homeLineup = homeLineup.toResponse(),
            awayLineup = awayLineup.toResponse(),
        )

    private fun Lineup.toResponse(): LineupResponse =
        LineupResponse(
            p1 = p1,
            p2 = p2,
            p3 = p3,
            p4 = p4,
            p5 = p5,
            p6 = p6,
        )

    private fun MatchTeam.toResponse(): MatchTeamResponse =
        MatchTeamResponse(
            teamId = teamId,
            code = code,
            players = players,
        )

    private fun Score.toResponse(): ScoreResponse =
        ScoreResponse(
            home = home,
            away = away,
        )

    private fun PlayAction.toResponse(): PlayActionResponse =
        when (this) {
            is PlayAction.Attack -> this.toResponse()
            is PlayAction.Block -> this.toResponse()
            is PlayAction.Dig -> this.toResponse()
            is PlayAction.Freeball -> this.toResponse()
            is PlayAction.Receive -> this.toResponse()
            is PlayAction.Serve -> this.toResponse()
            is PlayAction.Set -> this.toResponse()
        }

    private fun PlayAction.GeneralInfo.toResponse(): PlayActionResponse.GeneralInfoResponse =
        PlayActionResponse.GeneralInfoResponse(
            playerInfo = playerInfo.toResponse(),
            effect = effect,
            breakPoint = breakPoint,
        )

    private fun PlayAction.PlayerInfo.toResponse(): PlayActionResponse.PlayerInfoResponse =
        PlayActionResponse.PlayerInfoResponse(
            playerId = playerId,
            position = position,
            teamId = teamId,
        )

    private fun PlayAction.Attack.toResponse(): PlayActionResponse.AttackResponse =
        PlayActionResponse.AttackResponse(
            generalInfo = generalInfo.toResponse(),
            sideOut = sideOut,
            blockAttempt = blockAttempt,
            digAttempt = digAttempt,
            receiveEffect = receiveEffect,
            receiverId = receiverId,
            setEffect = setEffect,
            setterId = setterId,
        )

    private fun PlayAction.Block.toResponse(): PlayActionResponse.BlockResponse =
        PlayActionResponse.BlockResponse(
            generalInfo = generalInfo.toResponse(),
            attackerId = attackerId,
            setterId = setterId,
            afterSideOut = afterSideOut,
        )

    private fun PlayAction.Dig.toResponse(): PlayActionResponse.DigResponse =
        PlayActionResponse.DigResponse(
            generalInfo = generalInfo.toResponse(),
            attackerId = attackerId,
            rebounderId = rebounderId,
            afterSideOut = afterSideOut,
        )

    private fun PlayAction.Set.toResponse(): PlayActionResponse.SetResponse =
        PlayActionResponse.SetResponse(
            generalInfo = generalInfo.toResponse(),
            attackerId = attackerId,
            attackerPosition = attackerPosition,
            attackEffect = attackEffect,
            sideOut = sideOut,
        )

    private fun PlayAction.Freeball.toResponse(): PlayActionResponse.FreeballResponse =
        PlayActionResponse.FreeballResponse(
            generalInfo = generalInfo.toResponse(),
            afterSideOut = afterSideOut,
        )

    private fun PlayAction.Receive.toResponse(): PlayActionResponse.ReceiveResponse =
        PlayActionResponse.ReceiveResponse(
            generalInfo = generalInfo.toResponse(),
            serverId = serverId,
            attackEffect = attackEffect,
            setEffect = setEffect,
        )

    private fun PlayAction.Serve.toResponse(): PlayActionResponse.ServeResponse =
        PlayActionResponse.ServeResponse(
            generalInfo = generalInfo.toResponse(),
            receiverId = receiverId,
            receiveEffect = receiveEffect,
        )

    override fun from(from: MatchReportResponse): MatchStatistics =
        MatchStatistics(
            matchId = from.matchId,
            sets = from.sets.map { it.toDomain() },
            home = from.home.toDomain(),
            away = from.away.toDomain(),
            mvp = from.mvp,
            bestPlayer = from.bestPlayer,
            updatedAt = from.updatedAt,
            phase = from.phase,
        )

    private fun MatchSetResponse.toDomain(): MatchSet =
        MatchSet(
            number = number,
            score = score.toDomain(),
            points = points.map { it.toDomain() },
            startTime = startTime,
            endTime = endTime,
            duration = duration,
        )

    private fun MatchPointResponse.toDomain(): MatchPoint =
        MatchPoint(
            score = score.toDomain(),
            startTime = startTime,
            endTime = endTime,
            playActions = playActions.map { it.toDomain() },
            point = point,
            homeLineup = homeLineup.toDomain(),
            awayLineup = awayLineup.toDomain(),
        )

    private fun LineupResponse.toDomain(): Lineup =
        Lineup(
            p1 = p1,
            p2 = p2,
            p3 = p3,
            p4 = p4,
            p5 = p5,
            p6 = p6,
        )

    private fun MatchTeamResponse.toDomain(): MatchTeam =
        MatchTeam(
            teamId = teamId,
            code = code,
            players = players,
        )

    private fun ScoreResponse.toDomain(): Score =
        Score(
            home = home,
            away = away,
        )

    private fun PlayActionResponse.toDomain(): PlayAction =
        when (this) {
            is PlayActionResponse.AttackResponse -> this.toDomain()
            is PlayActionResponse.BlockResponse -> this.toDomain()
            is PlayActionResponse.DigResponse -> this.toDomain()
            is PlayActionResponse.FreeballResponse -> this.toDomain()
            is PlayActionResponse.ReceiveResponse -> this.toDomain()
            is PlayActionResponse.ServeResponse -> this.toDomain()
            is PlayActionResponse.SetResponse -> this.toDomain()
        }

    private fun PlayActionResponse.GeneralInfoResponse.toDomain(): PlayAction.GeneralInfo =
        PlayAction.GeneralInfo(
            playerInfo = playerInfo.toDomain(),
            effect = effect,
            breakPoint = breakPoint,
        )

    private fun PlayActionResponse.PlayerInfoResponse.toDomain(): PlayAction.PlayerInfo =
        PlayAction.PlayerInfo(
            playerId = playerId,
            position = position,
            teamId = teamId,
        )

    private fun PlayActionResponse.AttackResponse.toDomain(): PlayAction.Attack =
        PlayAction.Attack(
            generalInfo = generalInfo.toDomain(),
            sideOut = sideOut,
            blockAttempt = blockAttempt,
            digAttempt = digAttempt,
            receiveEffect = receiveEffect,
            receiverId = receiverId,
            setEffect = setEffect,
            setterId = setterId,
        )

    private fun PlayActionResponse.BlockResponse.toDomain(): PlayAction.Block =
        PlayAction.Block(
            generalInfo = generalInfo.toDomain(),
            attackerId = attackerId,
            setterId = setterId,
            afterSideOut = afterSideOut,
        )

    private fun PlayActionResponse.DigResponse.toDomain(): PlayAction.Dig =
        PlayAction.Dig(
            generalInfo = generalInfo.toDomain(),
            attackerId = attackerId,
            rebounderId = rebounderId,
            afterSideOut = afterSideOut,
        )

    private fun PlayActionResponse.SetResponse.toDomain(): PlayAction.Set =
        PlayAction.Set(
            generalInfo = generalInfo.toDomain(),
            attackerId = attackerId,
            attackerPosition = attackerPosition,
            attackEffect = attackEffect,
            sideOut = sideOut,
        )

    private fun PlayActionResponse.FreeballResponse.toDomain(): PlayAction.Freeball =
        PlayAction.Freeball(
            generalInfo = generalInfo.toDomain(),
            afterSideOut = afterSideOut,
        )

    private fun PlayActionResponse.ReceiveResponse.toDomain(): PlayAction.Receive =
        PlayAction.Receive(
            generalInfo = generalInfo.toDomain(),
            serverId = serverId,
            attackEffect = attackEffect,
            setEffect = setEffect,
        )

    private fun PlayActionResponse.ServeResponse.toDomain(): PlayAction.Serve =
        PlayAction.Serve(
            generalInfo = generalInfo.toDomain(),
            receiverId = receiverId,
            receiveEffect = receiveEffect,
        )
}
