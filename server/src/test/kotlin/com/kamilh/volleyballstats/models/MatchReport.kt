package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.utils.localDateTime

fun matchReportOf(
    matchId: MatchReportId = MatchReportId(0),
    phase: Phase = Phase.PlayOff,
    scout: Scout = scoutOf(),
    scoutData: List<List<ScoutData>> = emptyList(),
    matchTeams: MatchTeams = teamsOf(),
): RawMatchReport = RawMatchReport(
    matchId = matchId,
    phase = phase,
    scout = scout,
    scoutData = scoutData,
    matchTeams = matchTeams,
)

fun scoutOf(
    bestPlayer: BestPlayer? = null,
    ended: LocalDateTime = localDateTime(),
    mvp: Mvp = mvpOf(),
    sets: List<Set> = emptyList(),
): Scout = Scout(
    bestPlayer = bestPlayer,
    ended = ended,
    mvp = mvp,
    sets = sets,
)

fun setOf(
    duration: Int = 0,
    endTime: LocalDateTime = localDateTime(),
    events: List<Event> = emptyList(),
    matchScore: MatchScore = scoreOf(),
    startTime: LocalDateTime = localDateTime(),
    startingLineup: StartingLineup = startingLineupOf(),
): Set = Set(
    duration = duration,
    endTime = endTime,
    events = events,
    matchScore = matchScore,
    startTime = startTime,
    startingLineup = startingLineup,
)

fun mvpOf(
    number: Int = 0,
    team: TeamType = TeamType.Away,
): Mvp = Mvp(
    number = number,
    team = team,
)

fun startingLineupOf(
    away: List<Int> = emptyList(),
    home: List<Int> = emptyList(),
): StartingLineup = StartingLineup(
    away = away,
    home = home,
)

fun scoreOf(
    away: Int = 0,
    home: Int = 0,
): MatchScore = MatchScore(
    away = away,
    home = home,
)

fun teamsOf(
    away: MatchReportTeam = matchReportTeamOf(),
    home: MatchReportTeam = matchReportTeamOf(),
): MatchTeams = MatchTeams(
    away = away,
    home = home,
)

fun matchReportTeamOf(
    code: String = "",
    libero: List<Int> = emptyList(),
    name: String = "",
    players: List<MatchReportPlayer> = emptyList(),
): MatchReportTeam = MatchReportTeam(
    code = code,
    libero = libero,
    name = name,
    players = players,
)

fun matchTeamsOf(
    home: MatchReportTeam = matchReportTeamOf(),
    away: MatchReportTeam = matchReportTeamOf(),
): MatchTeams = MatchTeams(
    away = away,
    home = home,
)

fun matchReportPlayerOf(
    id: PlayerId = PlayerId(0),
    firstName: String = "",
    isForeign: Boolean? = null,
    lastName: String = "",
    shirtNumber: Int = 0,
): MatchReportPlayer = MatchReportPlayer(
    id = id,
    firstName = firstName,
    isForeign = isForeign,
    lastName = lastName,
    shirtNumber = shirtNumber,
)

fun liberoOf(
    enters: Boolean = false,
    libero: Int = 0,
    player: Int = 0,
    team: TeamType = TeamType.Away,
    time: LocalDateTime = localDateTime(),
): Event.Libero = Event.Libero(
    enters = enters,
    libero = libero,
    player = player,
    team = team,
    time = time,
)

fun rallyOf(
    endTime: LocalDateTime = localDateTime(),
    point: TeamType? = null,
    startTime: LocalDateTime = localDateTime(),
): Event.Rally = Event.Rally(
    endTime = endTime,
    point = point,
    startTime = startTime,
)

fun videoChallengeOf(
    atScore: AtScore = atScoreOf(),
    endTime: LocalDateTime = localDateTime(),
    reason: String = "",
    response: Event.VideoChallenge.Response = Event.VideoChallenge.Response.Inconclusive,
    scoreChange: Event.VideoChallenge.ScoreChange = Event.VideoChallenge.ScoreChange.NoChange,
    startTime: LocalDateTime = localDateTime(),
    team: TeamType = TeamType.Away,
): Event.VideoChallenge = Event.VideoChallenge(
    atScore = atScore,
    endTime = endTime,
    reason = reason,
    response = response,
    scoreChange = scoreChange,
    startTime = startTime,
    team = team,
)

fun atScoreOf(
    away: Int = 0,
    home: Int = 0,
): AtScore = AtScore(
    away = away,
    home = home,
)