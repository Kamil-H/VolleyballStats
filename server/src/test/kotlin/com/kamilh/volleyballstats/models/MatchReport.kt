package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.utils.localDateTime

fun matchReportOf(
    id: String = "",
    category: String = "",
    city: String? = null,
    competition: String? = null,
    createdAt: LocalDateTime = localDateTime(),
    division: String = "",
    hall: String = "",
    matchId: MatchReportId = MatchReportId(0),
    matchNumber: String? = null,
    officials: Officials = officialsOf(),
    phase: Phase = Phase.PlayOff,
    remarks: String? = null,
    commissionerRemarks: String? = null,
    scout: Scout = scoutOf(),
    scoutData: List<List<ScoutData>> = emptyList(),
    settings: Settings = settingsOf(),
    spectators: Int = 0,
    startDate: String = "",
    matchTeams: MatchTeams = teamsOf(),
    updatedAt: String = "",
): MatchReport = MatchReport(
    id = id,
    category = category,
    city = city,
    competition = competition,
    createdAt = createdAt,
    division = division,
    hall = hall,
    matchId = matchId,
    matchNumber = matchNumber,
    officials = officials,
    phase = phase,
    remarks = remarks,
    commissionerRemarks = commissionerRemarks,
    scout = scout,
    scoutData = scoutData,
    settings = settings,
    spectators = spectators,
    startDate = startDate,
    matchTeams = matchTeams,
    updatedAt = updatedAt,
)

fun officialsOf(
    supervisor: Supervisor? = null,
    commissioner: Commissioner = commissionerOf(),
    referee1: Referee = refereeOf(),
    referee2: Referee = refereeOf(),
    scorer1: Scorer? = null,
    scorer2: Scorer? = null,
    lineJudge1: LineJudge? = null,
    lineJudge2: LineJudge? = null,
): Officials = Officials(
    supervisor = supervisor,
    commissioner = commissioner,
    referee1 = referee1,
    referee2 = referee2,
    scorer1 = scorer1,
    scorer2 = scorer2,
    lineJudge1 = lineJudge1,
    lineJudge2 = lineJudge2,
)

fun commissionerOf(
    firstName: String = "",
    lastName: String = "",
): Commissioner = Commissioner(
    firstName = firstName,
    lastName = lastName,
)

fun refereeOf(
    firstName: String = "",
    lastName: String = "",
    level: String = "",
): Referee = Referee(
    firstName = firstName,
    lastName = lastName,
    level = level,
)

fun scorerOf(
    firstName: String = "",
    lastName: String = "",
    level: String? = null,
): Scorer = Scorer(
    firstName = firstName,
    lastName = lastName,
    level = level,
)

fun scoutOf(
    bestPlayer: BestPlayer? = null,
    coinToss: CoinToss = coinTossOf(),
    ended: LocalDateTime = localDateTime(),
    mvp: Mvp = mvpOf(),
    sets: List<Set> = emptyList(),
): Scout = Scout(
    bestPlayer = bestPlayer,
    coinToss = coinToss,
    ended = ended,
    mvp = mvp,
    sets = sets,
)

fun setOf(
    duration: Int = 0,
    endTime: LocalDateTime = localDateTime(),
    events: List<Event> = emptyList(),
    score: Score = scoreOf(),
    startTime: LocalDateTime = localDateTime(),
    startingLineup: StartingLineup = startingLineupOf(),
): Set = Set(
    duration = duration,
    endTime = endTime,
    events = events,
    score = score,
    startTime = startTime,
    startingLineup = startingLineup,
)

fun scoutDataOf(
    id: String = "",
    plays: List<Play> = emptyList(),
    point: TeamType = TeamType.Away,
    score: Score = scoreOf(),
): ScoutData = ScoutData(
    id = id,
    plays = plays,
    point = point,
    score = score,
)

fun bestPlayerOf(
    number: Int = 0,
    team: TeamType = TeamType.Away,
): BestPlayer = BestPlayer(
    number = number,
    team = team,
)

fun coinTossOf(
    start: Start = startOf(),
    deciding: Deciding? = null,
): CoinToss = CoinToss(
    start = start,
    deciding = deciding,
)

fun startOf(
    leftSide: String = "",
    serve: String = "",
): Start = Start(
    leftSide = leftSide,
    serve = serve,
)

fun decidingOf(
    leftSide: String = "",
    serve: String = "",
): Deciding = Deciding(
    leftSide = leftSide,
    serve = serve,
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
): Score = Score(
    away = away,
    home = home,
)

fun settingsOf(
    decidingSetWin: Int = 0,
    regularSetWin: Int = 0,
    winningScore: Int = 0,
): Settings = Settings(
    decidingSetWin = decidingSetWin,
    regularSetWin = regularSetWin,
    winningScore = winningScore,
)

fun teamsOf(
    away: MatchReportTeam = matchReportTeamOf(),
    home: MatchReportTeam = matchReportTeamOf(),
): MatchTeams = MatchTeams(
    away = away,
    home = home,
)

fun matchReportTeamOf(
    captain: Int = 0,
    code: String = "",
    libero: List<Int> = emptyList(),
    name: String = "",
    players: List<MatchReportPlayer> = emptyList(),
    shortName: String = "",
    staff: Staff = staffOf(),
): MatchReportTeam = MatchReportTeam(
    captain = captain,
    code = code,
    libero = libero,
    name = name,
    players = players,
    shortName = shortName,
    staff = staff,
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

fun staffOf(
    assistant1: Assistant? = null,
    assistant2: Assistant? = null,
    coach: Coach = coachOf(),
    medical1: Medical? = null,
    medical2: Medical? = null,
): Staff = Staff(
    assistant1 = assistant1,
    assistant2 = assistant2,
    coach = coach,
    medical1 = medical1,
    medical2 = medical2,
)

fun assistantOf(
    firstName: String = "",
    lastName: String = "",
): Assistant = Assistant(
    firstName = firstName,
    lastName = lastName,
)

fun coachOf(
    firstName: String = "",
    lastName: String = "",
): Coach = Coach(
    firstName = firstName,
    lastName = lastName,
)

fun medicalOf(
    firstName: String = "",
    lastName: String = "",
    type: String = "",
): Medical = Medical(
    firstName = firstName,
    lastName = lastName,
    type = type,
)

fun eventOf(
    libero: Event.Libero? = null,
    rally: Event.Rally? = null,
    sanction: Event.Sanction? = null,
    improperRequest: Event.ImproperRequest? = null,
    delay: Event.Delay? = null,
    injury: Event.Injury? = null,
    newLibero: Event.NewLibero? = null,
    substitution: Event.Substitution? = null,
    timeout: Event.Timeout? = null,
    videoChallenge: Event.VideoChallenge? = null,
): Event = libero ?: rally ?: sanction ?: improperRequest ?: delay ?: injury ?: newLibero ?: substitution ?: timeout
?: videoChallenge ?: error("")

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
    verified: Boolean? = null,
    startTime: LocalDateTime = localDateTime(),
): Event.Rally = Event.Rally(
    endTime = endTime,
    point = point,
    verified = verified,
    startTime = startTime,
)

fun sanctionOf(
    team: TeamType = TeamType.Away,
    type: String = "",
    player: Int? = null,
    time: LocalDateTime = localDateTime(),
    staff: String? = null,
): Event.Sanction = Event.Sanction(
    team = team,
    type = type,
    player = player,
    time = time,
    staff = staff,
)

fun delayOf(
    team: TeamType = TeamType.Away,
    time: LocalDateTime = localDateTime(),
): Event.Delay = Event.Delay(
    team = team,
    time = time,
)

fun substitutionOf(
    `in`: Int = 0,
    `out`: Int = 0,
    team: TeamType = TeamType.Away,
    time: LocalDateTime = localDateTime(),
): Event.Substitution = Event.Substitution(
    `in` = `in`,
    `out` = `out`,
    team = team,
    time = time,
)

fun timeoutOf(
    team: TeamType = TeamType.Away,
    time: LocalDateTime = localDateTime(),
): Event.Timeout = Event.Timeout(
    team = team,
    time = time,
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

fun playOf(
    id: String = "",
    effect: Effect = Effect.Perfect,
    player: Int = 0,
    skill: Skill = Skill.Attack,
    team: TeamType = TeamType.Away,
): Play = Play(
    id = id,
    effect = effect,
    player = player,
    skill = skill,
    team = team,
)