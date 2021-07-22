package com.kamilh.models

import java.time.LocalDateTime

sealed class PlayAction {

    abstract val generalInfo: GeneralInfo

    data class PlayerInfo(
        val playerId: PlayerId,
        val position: PlayerPosition,
        val teamId: TeamId,
    )

    data class GeneralInfo(
        val playerInfo: PlayerInfo,
        val matchId: MatchReportId,
        val set: Int,
        val effect: Effect,
        val currentScore: CurrentScore,
        val rallyStartTime: LocalDateTime,
        val rallyEndTime: LocalDateTime,
    )

    data class Attack(
        override val generalInfo: GeneralInfo,
        val sideOut: Boolean,
        val blockAttempt: Boolean,
        val digAttempt: Boolean,
        val receiveEffect: Effect?,
        val receiverId: PlayerId?,
        val setEffect: Effect?,
        val setterId: PlayerId?,
    ) : PlayAction()

    data class Block(
        override val generalInfo: GeneralInfo,
        val attackerId: PlayerId,
        val setterId: PlayerId?,
        val afterSideOut: Boolean,
    ) : PlayAction()

    data class Dig(
        override val generalInfo: GeneralInfo,
        val attackerId: PlayerId?,
        val rebounderId: PlayerId?,
        val afterSideOut: Boolean,
    ) : PlayAction()

    data class Set(
        override val generalInfo: GeneralInfo,
        val attackerId: PlayerId,
        val sideOut: Boolean,
    ) : PlayAction()

    data class Freeball(
        override val generalInfo: GeneralInfo,
        val afterSideOut: Boolean,
    ) : PlayAction()

    data class Receive(
        override val generalInfo: GeneralInfo,
        val serverId: PlayerId,
        val attackEffect: Effect?,
        val setEffect: Effect?,
    ) : PlayAction()

    data class Serve(
        override val generalInfo: GeneralInfo,
        val receiverId: PlayerId?,
        val receiveEffect: Effect?,
    ) : PlayAction()
}