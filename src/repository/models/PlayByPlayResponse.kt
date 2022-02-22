@file:UseSerializers(LocalDateTimeSerializer::class)

package com.kamilh.repository.models

import com.kamilh.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject
import java.time.LocalDateTime

@Serializable
class PlayByPlayResponse(
    val total: Int,
    val limit: Int,
    val skip: Int,
    val data: List<MatchResponse>,
)

@Serializable
class MatchResponse(
    val _id: String,
    val category: String,
    val city: String,
    val competition: String,
    val createdAt: LocalDateTime,
    val division: String,
    val hall: String,
    val matchId: Int,
    val matchNumber: String? = null,
    val officials: OfficialsResponse,
    val phase: String,
    val remarks: String? = null,
    val commissionerRemarks: String? = null,
    val scout: ScoutResponse,
    val scoutData: List<List<ScoutDataResponse>>,
    val settings: SettingsResponse,
    val spectators: Int,
    val startDate: String,
    val teams: TeamsResponse,
    val updatedAt: String,
    val fullScoutData: JsonObject? = null,
)

@Serializable
class OfficialsResponse(
    val supervisor: SupervisorResponse? = null,
    val commissioner: CommissionerResponse,
    val referee1: RefereeResponse,
    val referee2: RefereeResponse,
    val scorer1: ScorerResponse? = null,
    val scorer2: ScorerResponse? = null,
    val lineJudge1: LineJudgeResponse? = null,
    val lineJudge2: LineJudgeResponse? = null,
)

@Serializable
class ScoutResponse(
    val bestPlayer: BestPlayerResponse? = null,
    val coinToss: CoinTossResponse,
    val ended: LocalDateTime,
    val mvp: MvpResponse,
    val sets: List<SetResponse>,
)

@Serializable
class ScoutDataResponse(
    val _id: String,
    val plays: List<PlayResponse>,
    val point: String,
    val score: ScoreResponse,
)

@Serializable
class SettingsResponse(
    val decidingSetWin: Int,
    val regularSetWin: Int,
    val winningScore: Int,
)

@Serializable
class TeamsResponse(
    val away: TeamResponse,
    val home: TeamResponse,
)

@Serializable
class CommissionerResponse(
    val firstName: String,
    val lastName: String,
)

@Serializable
class SupervisorResponse(
    val firstName: String,
    val lastName: String,
)

@Serializable
class RefereeResponse(
    val firstName: String,
    val lastName: String,
    val level: String,
)

@Serializable
class ScorerResponse(
    val firstName: String,
    val lastName: String,
    val level: String? = null,
)

@Serializable
class BestPlayerResponse(
    val number: Int,
    val team: String,
)

@Serializable
class CoinTossResponse(
    val start: StartResponse,
    val deciding: DecidingResponse? = null,
)

@Serializable
class MvpResponse(
    val number: Int,
    val team: String,
)

@Serializable
class SetResponse(
    val duration: Int,
    val endTime: LocalDateTime,
    val events: List<EventResponse>,
    val score: ScoreResponse,
    val startTime: LocalDateTime,
    val startingLineup: StartingLineupResponse,
)

@Serializable
class StartResponse(
    val leftSide: String,
    val serve: String,
)

@Serializable
class DecidingResponse(
    val leftSide: String,
    val serve: String,
)

@Serializable
class EventResponse(
    val libero: LiberoResponse? = null,
    val rally: RallyResponse? = null,
    val sanction: SanctionResponse? = null,
    val improperRequest: ImproperRequestResponse? = null,
    val delay: DelayResponse? = null,
    val injury: InjuryResponse? = null,
    val newLibero: NewLiberoResponse? = null,
    val substitution: SubstitutionResponse? = null,
    val timeout: TimeoutResponse? = null,
    val videoChallenge: VideoChallengeResponse? = null,
    val manualChange: ManualChangeResponse? = null,
)

@Serializable
class NewLiberoResponse(
    val team: String,
    val player: Int,
    val time: LocalDateTime,
)

@Serializable
class InjuryResponse(
    val team: String,
    val player: Int,
    val time: LocalDateTime,
    val libero: Boolean,
)

@Serializable
class ImproperRequestResponse(
    val team: String,
    val time: LocalDateTime,
)

@Serializable
class ScoreResponse(
    val away: Int,
    val home: Int,
)

@Serializable
class StartingLineupResponse(
    val away: List<Int>,
    val home: List<Int>,
)

@Serializable
class LiberoResponse(
    val enters: Boolean,
    val libero: Int,
    val player: Int,
    val team: String,
    val time: LocalDateTime,
)

@Serializable
class RallyResponse(
    val endTime: LocalDateTime? = null,
    val point: String? = null,
    val verified: Boolean? = null,
    val startTime: LocalDateTime,
)

@Serializable
class SanctionResponse(
    val team: String,
    val type: String,
    val player: Int? = null,
    val time: LocalDateTime,
    val staff: String? = null,
)

@Serializable
class DelayResponse(
    val team: String,
    val time: LocalDateTime,
)

@Serializable
class SubstitutionResponse(
    val `in`: Int,
    val `out`: Int,
    val team: String,
    val time: LocalDateTime,
)

@Serializable
class TimeoutResponse(
    val team: String,
    val time: LocalDateTime,
)

@Serializable
class VideoChallengeResponse(
    val atScore: AtScoreResponse,
    val endTime: LocalDateTime,
    val reason: String,
    val response: String,
    val scoreChange: String? = null,
    val startTime: LocalDateTime,
    val team: String,
)

@Serializable
class ManualChangeResponse(
    val score: ScoreResponse,
    val lineup: StartingLineupResponse,
    val serve: String,
)

@Serializable
class AtScoreResponse(
    val away: Int,
    val home: Int,
)

@Serializable
class PlayResponse(
    val _id: String,
    val effect: Char,
    val player: Int,
    val skill: Char,
    val team: String,
)

@Serializable
class TeamResponse(
    val captain: Int,
    val code: String,
    val libero: List<Int>,
    val name: String,
    val players: List<PlayerResponse>,
    val shortName: String,
    val staff: StaffResponse,
)

@Serializable
class PlayerResponse(
    val code: String,
    val firstName: String,
    val isForeign: Boolean? = null,
    val lastName: String,
    val shirtNumber: Int,
)

@Serializable
class StaffResponse(
    val assistant1: AssistantResponse? = null,
    val assistant2: AssistantResponse? = null,
    val coach: CoachResponse,
    val medical1: MedicalResponse? = null,
    val medical2: MedicalResponse? = null,
)

@Serializable
class AssistantResponse(
    val firstName: String,
    val lastName: String,
)

@Serializable
class CoachResponse(
    val firstName: String,
    val lastName: String,
)

@Serializable
class MedicalResponse(
    val firstName: String,
    val lastName: String,
    val type: String,
)

@Serializable
class LineJudgeResponse(
    val firstName: String,
    val lastName: String,
)