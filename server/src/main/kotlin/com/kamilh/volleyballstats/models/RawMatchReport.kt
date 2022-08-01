package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Skill

data class RawMatchReport(
    val matchId: MatchReportId,
    val phase: Phase,
    val scout: Scout,
    val scoutData: List<List<ScoutData>>,
    val matchTeams: MatchTeams,
)

data class Scout(
    val bestPlayer: BestPlayer?,
    val ended: LocalDateTime,
    val mvp: Mvp,
    val sets: List<Set>
)

data class ScoutData(
    val id: String,
    val plays: List<Play>,
    val point: TeamType,
    val matchScore: MatchScore,
)

data class MatchTeams(
    val away: MatchReportTeam,
    val home: MatchReportTeam,
)

data class BestPlayer(
    val number: Int,
    val team: TeamType,
)

data class Mvp(
    val number: Int,
    val team: TeamType,
)

data class Set(
    val duration: Int,
    val endTime: LocalDateTime,
    val events: List<Event>,
    val matchScore: MatchScore,
    val startTime: LocalDateTime,
    val startingLineup: StartingLineup
)

sealed class Event {

    /**
     * It must be nullable, because [ManualChange] doesn't have an information about time
     */
    abstract val time: LocalDateTime?

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

    @Suppress("ConstructorParameterNaming")
    data class Substitution(
        val `in`: Int,
        val out: Int,
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

        enum class Response {
            Right, Wrong, Inconclusive;
        }
        enum class ScoreChange {
            AssignToOther, RepeatLast, NoChange;
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

    data class ManualChange(
        val matchScore: MatchScore,
        val lineup: StartingLineup,
        val serve: TeamType,
        override val time: LocalDateTime?,
    ) : Event()
}

data class MatchScore(
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

data class MatchReportTeam(
    val code: String,
    val libero: List<Int>,
    val name: String,
    val players: List<MatchReportPlayer>,
)

enum class TeamType {
    Home, Away;
}

data class MatchReportPlayer(
    val id: PlayerId,
    val firstName: String,
    val isForeign: Boolean?,
    val lastName: String,
    val shirtNumber: Int
)
