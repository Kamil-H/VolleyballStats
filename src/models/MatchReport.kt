package com.kamilh.models

import java.time.LocalDateTime

data class MatchReport(
    val id: String,
    val category: String,
    val city: String,
    val competition: String,
    val createdAt: LocalDateTime,
    val division: String,
    val hall: String,
    val matchId: MatchReportId,
    val matchNumber: String?,
    val officials: Officials,
    val phase: String,
    val remarks: String?,
    val commissionerRemarks: String?,
    val scout: Scout,
    val scoutData: List<List<ScoutData>>,
    val settings: Settings,
    val spectators: Int,
    val startDate: String,
    val matchTeams: MatchTeams,
    val updatedAt: String
)

data class Officials(
    val supervisor: Supervisor?,
    val commissioner: Commissioner,
    val referee1: Referee,
    val referee2: Referee,
    val scorer1: Scorer,
    val lineJudge1: LineJudge?,
    val lineJudge2: LineJudge?,
)

data class Scout(
    val bestPlayer: BestPlayer?,
    val coinToss: CoinToss,
    val ended: LocalDateTime,
    val mvp: Mvp,
    val sets: List<Set>
)

data class ScoutData(
    val id: String,
    val plays: List<Play>,
    val point: TeamType,
    val score: Score,
)

data class Settings(
    val decidingSetWin: Int,
    val regularSetWin: Int,
    val winningScore: Int,
)

data class MatchTeams(
    val away: MatchTeam,
    val home: MatchTeam,
)

data class Commissioner(
    val firstName: String,
    val lastName: String,
)

data class Referee(
    val firstName: String,
    val lastName: String,
    val level: String,
)

data class Scorer(
    val firstName: String,
    val lastName: String,
    val level: String,
)

data class BestPlayer(
    val number: Int,
    val team: TeamType,
)

data class CoinToss(
    val start: Start,
    val deciding: Deciding?,
)

data class Deciding(
    val leftSide: String,
    val serve: String
)

data class Mvp(
    val number: Int,
    val team: TeamType,
)

data class Set(
    val duration: Int,
    val endTime: LocalDateTime,
    val events: List<Event>,
    val score: Score,
    val startTime: LocalDateTime,
    val startingLineup: StartingLineup
)

data class Start(
    val leftSide: String,
    val serve: String
)

sealed class Event {

    abstract val time: LocalDateTime

    data class Libero(
        val enters: Boolean,
        val libero: Int,
        val player: Int,
        val team: TeamType,
        override val time: LocalDateTime
    ) : Event()

    /**
     * [point] is null only when referee decides that the rally needs to be repeated
     */
    data class Rally(
        val endTime: LocalDateTime,
        val point: TeamType?,
        val startTime: LocalDateTime,
        val verified: Boolean?,
    ) : Event() {
        override val time: LocalDateTime = startTime
    }

    data class Substitution(
        val `in`: Int,
        val `out`: Int,
        val team: TeamType,
        override val time: LocalDateTime
    ) : Event()

    data class Timeout(
        val team: TeamType,
        override val time: LocalDateTime
    ) : Event()

    data class VideoChallenge(
        val atScore: AtScore,
        val endTime: LocalDateTime,
        val reason: String,
        val response: Response,
        val scoreChange: ScoreChange?,
        val startTime: LocalDateTime,
        val team: TeamType
    ) : Event() {
        override val time: LocalDateTime = startTime

        enum class Response(val value: String) {
            Right("right"), Wrong("wrong"), Inconclusive("inconclusive");
            companion object {
                fun createOrNull(value: String?): Response? = values().firstOrNull { it.value == value }

                fun create(value: String): Response = createOrNull(value) ?: error("Wrong TeamType=$value")
            }
        }
        enum class ScoreChange(val value: String) {
            AssignToOther("assignToOther"), RepeatLast("repeatLast"), NoChange("noChange");
            companion object {
                fun createOrNull(value: String?): ScoreChange? = values().firstOrNull { it.value == value }

                fun create(value: String): ScoreChange = createOrNull(value) ?: error("Wrong TeamType=$value")
            }
        }
    }

    data class Sanction(
        val team: TeamType,
        val type: String,
        val player: Int?,
        override val time: LocalDateTime,
        val staff: String?,
    ) : Event()

    data class ImproperRequest(
        val team: TeamType,
        override val time: LocalDateTime,
    ) : Event()

    data class Delay(
        val team: TeamType,
        override val time: LocalDateTime,
    ) : Event()

    data class Injury(
        val team: TeamType,
        val player: Int,
        override val time: LocalDateTime,
        val libero: Boolean,
    ) : Event()

    data class NewLibero(
        val team: TeamType,
        val player: Int,
        override val time: LocalDateTime,
    ) : Event()
}

data class Score(
    val home: Int,
    val away: Int,
)

data class StartingLineup(
    val away: List<Int>,
    val home: List<Int>
)

data class AtScore(
    val away: Int,
    val home: Int
)

data class Play(
    val id: String,
    val effect: Effect,
    val player: Int,
    val skill: Skill,
    val team: TeamType,
)

data class MatchTeam(
    val captain: Int,
    val code: String,
    val libero: List<Int>,
    val name: String,
    val players: List<TeamPlayer>,
    val shortName: String,
    val staff: Staff
)

enum class TeamType(val value: String) {
    Home("home"), Away("away");

    companion object {
        fun createOrNull(value: String?): TeamType? = values().firstOrNull { it.value == value }

        fun create(value: String): TeamType = createOrNull(value) ?: error("Wrong TeamType=$value")
    }
}

data class TeamPlayer(
    val id: PlayerId,
    val firstName: String,
    val isForeign: Boolean?,
    val lastName: String,
    val shirtNumber: Int
)

data class Staff(
    val assistant1: Assistant?,
    val assistant2: Assistant?,
    val coach: Coach,
    val medical1: Medical?,
    val medical2: Medical?
)

data class Assistant(
    val firstName: String,
    val lastName: String
)

data class Coach(
    val firstName: String,
    val lastName: String
)

data class Medical(
    val firstName: String,
    val lastName: String,
    val type: String
)

data class Supervisor(
    val firstName: String,
    val lastName: String
)

data class LineJudge(
    val firstName: String,
    val lastName: String,
)