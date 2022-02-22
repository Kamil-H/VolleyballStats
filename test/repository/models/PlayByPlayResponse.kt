package com.kamilh.repository.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

fun playByPlayResponseOf(
    total: Int = 0,
    limit: Int = 0,
    skip: Int = 0,
    data: List<MatchResponse> = emptyList(),
): PlayByPlayResponse =
    PlayByPlayResponse(
        total = total,
        limit = limit,
        skip = skip,
        data = data,
    )

fun matchResponseOf(
    _id: String = "",
    category: String = "",
    city: String = "",
    competition: String = "",
    createdAt: LocalDateTime = LocalDateTime.now(),
    division: String = "",
    hall: String = "",
    matchId: Int = 0,
    matchNumber: String? = null,
    officials: OfficialsResponse = officialsResponseOf(),
    phase: String = "FZ",
    remarks: String? = null,
    commissionerRemarks: String? = null,
    scout: ScoutResponse = scoutResponseOf(),
    scoutData: List<List<ScoutDataResponse>> = emptyList(),
    settings: SettingsResponse = settingsResponseOf(),
    spectators: Int = 0,
    startDate: String = "",
    teams: TeamsResponse = teamsResponseOf(),
    updatedAt: String = "",
): MatchResponse = MatchResponse(
    _id = _id,
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
    teams = teams,
    updatedAt = updatedAt,
)

fun officialsResponseOf(
    supervisor: SupervisorResponse? = null,
    commissioner: CommissionerResponse = commissionerResponseOf(),
    referee1: RefereeResponse = refereeResponseOf(),
    referee2: RefereeResponse = refereeResponseOf(),
    scorer1: ScorerResponse? = null,
    scorer2: ScorerResponse? = null,
    lineJudge1: LineJudgeResponse? = null,
    lineJudge2: LineJudgeResponse? = null,
): OfficialsResponse =
    OfficialsResponse(
        supervisor = supervisor,
        commissioner = commissioner,
        referee1 = referee1,
        referee2 = referee2,
        scorer1 = scorer1,
        scorer2 = scorer2,
        lineJudge1 = lineJudge1,
        lineJudge2 = lineJudge2,
    )

fun commissionerResponseOf(
    firstName: String = "",
    lastName: String = "",
): CommissionerResponse =
    CommissionerResponse(
        firstName = firstName,
        lastName = lastName,
    )

fun refereeResponseOf(
    firstName: String = "",
    lastName: String = "",
    level: String = "",
): RefereeResponse =
    RefereeResponse(
        firstName = firstName,
        lastName = lastName,
        level = level,
    )

fun scorerResponseOf(
    firstName: String = "",
    lastName: String = "",
    level: String? = null,
): ScorerResponse =
    ScorerResponse(
        firstName = firstName,
        lastName = lastName,
        level = level,
    )

fun scoutResponseOf(
    bestPlayer: BestPlayerResponse? = null,
    coinToss: CoinTossResponse = coinTossResponseOf(),
    ended: LocalDateTime = LocalDateTime.now(),
    mvp: MvpResponse = mvpResponseOf(),
    sets: List<SetResponse> = emptyList(),
): ScoutResponse =
    ScoutResponse(
        bestPlayer = bestPlayer,
        coinToss = coinToss,
        ended = ended,
        mvp = mvp,
        sets = sets,
    )

fun coinTossResponseOf(
    start: StartResponse = startResponseOf(),
    deciding: DecidingResponse? = null,
): CoinTossResponse =
    CoinTossResponse(
        start = start,
        deciding = deciding,
    )

fun startResponseOf(
    leftSide: String = "",
    serve: String = "",
): StartResponse =
    StartResponse(
        leftSide = leftSide,
        serve = serve,
    )

fun decidingResponseOf(
    leftSide: String = "",
    serve: String = "",
): DecidingResponse =
    DecidingResponse(
        leftSide = leftSide,
        serve = serve,
    )

fun mvpResponseOf(
    number: Int = 0,
    team: String = "home",
): MvpResponse =
    MvpResponse(
        number = number,
        team = team,
    )

fun setResponseOf(
    duration: Int = 0,
    endTime: LocalDateTime = LocalDateTime.now(),
    events: List<EventResponse> = emptyList(),
    score: ScoreResponse = scoreResponseOf(),
    startTime: LocalDateTime = LocalDateTime.now(),
    startingLineup: StartingLineupResponse = startingLineupResponseOf(),
): SetResponse =
    SetResponse(
        duration = duration,
        endTime = endTime,
        events = events,
        score = score,
        startTime = startTime,
        startingLineup = startingLineup,
    )

fun startingLineupResponseOf(
    away: List<Int> = emptyList(),
    home: List<Int> = emptyList(),
): StartingLineupResponse =
    StartingLineupResponse(
        away = away,
        home = home,
    )

fun scoreResponseOf(
    away: Int = 0,
    home: Int = 0,
): ScoreResponse =
    ScoreResponse(
        away = away,
        home = home,
    )

fun settingsResponseOf(
    decidingSetWin: Int = 0,
    regularSetWin: Int = 0,
    winningScore: Int = 0,
): SettingsResponse =
    SettingsResponse(
        decidingSetWin = decidingSetWin,
        regularSetWin = regularSetWin,
        winningScore = winningScore,
    )

fun teamsResponseOf(
    away: TeamResponse = teamResponseOf(),
    home: TeamResponse = teamResponseOf(),
): TeamsResponse =
    TeamsResponse(
        away = away,
        home = home,
    )

fun teamResponseOf(
    captain: Int = 0,
    code: String = "",
    libero: List<Int> = emptyList(),
    name: String = "",
    players: List<PlayerResponse> = emptyList(),
    shortName: String = "",
    staff: StaffResponse = staffResponseOf(),
): TeamResponse =
    TeamResponse(
        captain = captain,
        code = code,
        libero = libero,
        name = name,
        players = players,
        shortName = shortName,
        staff = staff,
    )

fun playerResponseOf(
    code: String = "",
    firstName: String = "",
    isForeign: Boolean? = null,
    lastName: String = "",
    shirtNumber: Int = 0,
): PlayerResponse =
    PlayerResponse(
        code = code,
        firstName = firstName,
        isForeign = isForeign,
        lastName = lastName,
        shirtNumber = shirtNumber,
    )

fun staffResponseOf(
    assistant1: AssistantResponse? = null,
    assistant2: AssistantResponse? = null,
    coach: CoachResponse = coachResponseOf(),
    medical1: MedicalResponse? = null,
    medical2: MedicalResponse? = null,
): StaffResponse =
    StaffResponse(
        assistant1 = assistant1,
        assistant2 = assistant2,
        coach = coach,
        medical1 = medical1,
        medical2 = medical2,
    )

fun assistantResponseOf(
    firstName: String = "",
    lastName: String = "",
): AssistantResponse =
    AssistantResponse(
        firstName = firstName,
        lastName = lastName,
    )

fun coachResponseOf(
    firstName: String = "",
    lastName: String = "",
): CoachResponse =
    CoachResponse(
        firstName = firstName,
        lastName = lastName,
    )

fun medicalResponseOf(
    firstName: String = "",
    lastName: String = "",
    type: String = "",
): MedicalResponse =
    MedicalResponse(
        firstName = firstName,
        lastName = lastName,
        type = type,
    )

@Serializable
class MedicalResponse(
    val firstName: String,
    val lastName: String,
    val type: String,
)

fun eventResponseOf(
    libero: LiberoResponse? = null,
    rally: RallyResponse? = null,
    sanction: SanctionResponse? = null,
    improperRequest: ImproperRequestResponse? = null,
    delay: DelayResponse? = null,
    injury: InjuryResponse? = null,
    newLibero: NewLiberoResponse? = null,
    substitution: SubstitutionResponse? = null,
    timeout: TimeoutResponse? = null,
    videoChallenge: VideoChallengeResponse? = null,
): EventResponse =
    EventResponse(
        libero = libero,
        rally = rally,
        sanction = sanction,
        improperRequest = improperRequest,
        delay = delay,
        injury = injury,
        newLibero = newLibero,
        substitution = substitution,
        timeout = timeout,
        videoChallenge = videoChallenge,
    )

fun liberoResponseOf(
    enters: Boolean = false,
    libero: Int = 0,
    player: Int = 0,
    team: String = "",
    time: LocalDateTime = LocalDateTime.now(),
): LiberoResponse =
    LiberoResponse(
        enters = enters,
        libero = libero,
        player = player,
        team = team,
        time = time,
    )

fun rallyResponseOf(
    endTime: LocalDateTime = LocalDateTime.now(),
    point: String? = null,
    verified: Boolean? = null,
    startTime: LocalDateTime = LocalDateTime.now(),
): RallyResponse =
    RallyResponse(
        endTime = endTime,
        point = point,
        verified = verified,
        startTime = startTime,
    )

fun sanctionResponseOf(
    team: String = "",
    type: String = "",
    player: Int? = null,
    time: LocalDateTime = LocalDateTime.now(),
    staff: String? = null,
): SanctionResponse =
    SanctionResponse(
        team = team,
        type = type,
        player = player,
        time = time,
        staff = staff,
    )

fun delayResponseOf(
    team: String = "",
    time: LocalDateTime = LocalDateTime.now(),
): DelayResponse =
    DelayResponse(
        team = team,
        time = time,
    )

fun substitutionResponseOf(
    `in`: Int = 0,
    `out`: Int = 0,
    team: String = "",
    time: LocalDateTime = LocalDateTime.now(),
): SubstitutionResponse =
    SubstitutionResponse(
        `in` = `in`,
        `out` = `out`,
        team = team,
        time = time,
    )

fun timeoutResponseOf(
    team: String = "",
    time: LocalDateTime = LocalDateTime.now(),
): TimeoutResponse =
    TimeoutResponse(
        team = team,
        time = time,
    )

fun videoChallengeResponseOf(
    atScore: AtScoreResponse = atScoreResponseOf(),
    endTime: LocalDateTime = LocalDateTime.now(),
    reason: String = "",
    response: String = "",
    scoreChange: String? = null,
    startTime: LocalDateTime = LocalDateTime.now(),
    team: String = "",
): VideoChallengeResponse =
    VideoChallengeResponse(
        atScore = atScore,
        endTime = endTime,
        reason = reason,
        response = response,
        scoreChange = scoreChange,
        startTime = startTime,
        team = team,
    )

fun atScoreResponseOf(
    away: Int = 0,
    home: Int = 0,
): AtScoreResponse =
    AtScoreResponse(
        away = away,
        home = home,
    )

fun playResponseOf(
    _id: String = "",
    effect: Char = Char.MIN_VALUE,
    player: Int = 0,
    skill: Char = Char.MIN_VALUE,
    team: String = "",
): PlayResponse =
    PlayResponse(
        _id = _id,
        effect = effect,
        player = player,
        skill = skill,
        team = team,
    )