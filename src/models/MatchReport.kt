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
    val matchId: Int,
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
    val teams: Teams,
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
    val point: String,
    val score: Score,
)

data class Settings(
    val decidingSetWin: Int,
    val regularSetWin: Int,
    val winningScore: Int,
)

data class Teams(
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
    val team: String,
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
    val team: String
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

    data class Libero(
        val enters: Boolean,
        val libero: Int,
        val player: Int,
        val team: String,
        val time: LocalDateTime
    ) : Event()

    data class Rally(
        val endTime: LocalDateTime,
        val point: String?,
        val startTime: LocalDateTime,
        val verified: Boolean?,
    ) : Event()

    data class Substitution(
        val `in`: Int,
        val `out`: Int,
        val team: String,
        val time: LocalDateTime
    ) : Event()

    data class Timeout(
        val team: String,
        val time: LocalDateTime
    ) : Event()

    data class VideoChallenge(
        val atScore: AtScore,
        val endTime: LocalDateTime,
        val reason: String,
        val response: String,
        val scoreChange: String?,
        val startTime: LocalDateTime,
        val team: String
    ) : Event()

    data class Sanction(
        val team: String,
        val type: String,
        val player: Int?,
        val time: LocalDateTime,
        val staff: String?,
    ) : Event()

    data class ImproperRequest(
        val team: String,
        val time: LocalDateTime,
    ) : Event()

    data class Delay(
        val team: String,
        val time: LocalDateTime,
    ) : Event()

    data class Injury(
        val team: String,
        val player: Int,
        val time: LocalDateTime,
        val libero: Boolean,
    ) : Event()

    data class NewLibero(
        val team: String,
        val player: Int,
        val time: LocalDateTime,
    ) : Event()
}

data class Score(
    val away: Int,
    val home: Int
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
    val effect: String,
    val player: Int,
    val skill: String,
    val team: String
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

data class TeamPlayer(
    val code: String,
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