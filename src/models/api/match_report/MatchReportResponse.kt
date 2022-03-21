@file:UseSerializers(
    PlayerIdSerializer::class,
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class,
    ZonedDateTimeSerializer::class,
    MatchIdSerializer::class,
    DurationSerializer::class,
)

package com.kamilh.models.api.match_report

import com.kamilh.datetime.LocalDateTime
import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.*
import com.kamilh.models.api.adapters.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.time.Duration

@Serializable
class MatchReportResponse(
    val matchId: MatchId,
    val sets: List<MatchSetResponse>,
    val home: MatchTeamResponse,
    val away: MatchTeamResponse,
    val mvp: PlayerId,
    val bestPlayer: PlayerId?,
    val updatedAt: LocalDateTime,
    val phase: Phase,
)

@Serializable
class MatchSetResponse(
    val number: Int,
    val score: ScoreResponse,
    val points: List<MatchPointResponse>,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val duration: Duration,
)

@Serializable
class MatchPointResponse(
    val score: ScoreResponse,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val playActions: List<PlayActionResponse> = emptyList(),
    val point: TeamId,
    val homeLineup: LineupResponse,
    val awayLineup: LineupResponse,
)

@Serializable
class ScoreResponse(
    val home: Int,
    val away: Int,
)

@Serializable
class MatchTeamResponse(
    val teamId: TeamId,
    val code: String,
    val players: List<PlayerId>,
)

@Serializable
class LineupResponse(
    val p1: PlayerId,
    val p2: PlayerId,
    val p3: PlayerId,
    val p4: PlayerId,
    val p5: PlayerId,
    val p6: PlayerId,
)

@Serializable
sealed class PlayActionResponse {

    abstract val generalInfo: GeneralInfoResponse

    @Serializable
    class PlayerInfoResponse(
        val playerId: PlayerId,
        val position: PlayerPosition?,
        val teamId: TeamId,
    )

    @Serializable
     class GeneralInfoResponse(
        val playerInfo: PlayerInfoResponse,
        val effect: Effect,
        val breakPoint: Boolean,
    )

    @Serializable
     class AttackResponse(
        override val generalInfo: GeneralInfoResponse,
        val sideOut: Boolean,
        val blockAttempt: Boolean,
        val digAttempt: Boolean,
        val receiveEffect: Effect?,
        val receiverId: PlayerId?,
        val setEffect: Effect?,
        val setterId: PlayerId?,
    ) : PlayActionResponse()

    @Serializable
     class BlockResponse(
        override val generalInfo: GeneralInfoResponse,
        val attackerId: PlayerId,
        val setterId: PlayerId?,
        val afterSideOut: Boolean,
    ) : PlayActionResponse()

    @Serializable
     class DigResponse(
        override val generalInfo: GeneralInfoResponse,
        val attackerId: PlayerId?,
        val rebounderId: PlayerId?,
        val afterSideOut: Boolean,
    ) : PlayActionResponse()

    @Serializable
     class SetResponse(
        override val generalInfo: GeneralInfoResponse,
        val attackerId: PlayerId?,
        val attackerPosition: PlayerPosition?,
        val attackEffect: Effect?,
        val sideOut: Boolean,
    ) : PlayActionResponse()

    @Serializable
     class FreeballResponse(
        override val generalInfo: GeneralInfoResponse,
        val afterSideOut: Boolean,
    ) : PlayActionResponse()

    @Serializable
     class ReceiveResponse(
        override val generalInfo: GeneralInfoResponse,
        val serverId: PlayerId,
        val attackEffect: Effect?,
        val setEffect: Effect?,
    ) : PlayActionResponse()

    @Serializable
     class ServeResponse(
        override val generalInfo: GeneralInfoResponse,
        val receiverId: PlayerId?,
        val receiveEffect: Effect?,
    ) : PlayActionResponse()
}