package com.kamilh.models

import java.time.LocalDateTime

data class PlayerInfo(
    val playerId: Long,
    val position: String,
    val teamId: Long,
)

data class GeneralInfo(
    val playerInfo: PlayerInfo,
    val matchId: Long,
    val set: Int,
    val effect: String,
    val currentScore: Score,
    val rallyTime: LocalDateTime,
)

data class AttackRow(
    val generalInfo: GeneralInfo,
    val sideOut: Boolean,
    val blockAttempt: Boolean,
    val digAttempt: Boolean,
    val receiveEffect: String?,
    val receiverId: Long?,
    val setEffect: String?,
    val setterId: Long?,
)

data class BlockRow(
    val generalInfo: GeneralInfo,
    val attackerId: Long,
    val setterId: Long?,
    val afterSideOut: Boolean,
)

data class DigRow(
    val generalInfo: GeneralInfo,
    val attackerId: Long?,
    val rebounderId: Long?,
    val afterSideOut: Boolean,
)

data class SetRow(
    val generalInfo: GeneralInfo,
    val attackerId: Long,
    val sideOut: Boolean,
)

data class FreeballRow(
    val generalInfo: GeneralInfo,
    val afterSideOut: Boolean,
)

data class ReceiveRow(
    val generalInfo: GeneralInfo,
    val serverId: Long,
    val attackEffect: String?,
    val setEffect: String?,
)

data class ServeRow(
    val generalInfo: GeneralInfo,
    val receiverId: Long?,
    val receiveEffect: String?,
)