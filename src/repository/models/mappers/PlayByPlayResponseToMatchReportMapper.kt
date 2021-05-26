package com.kamilh.repository.models.mappers

import com.kamilh.models.*
import com.kamilh.models.Set
import com.kamilh.repository.models.*

class MatchResponseToMatchReportMapper {

    fun map(from: MatchResponse): MatchReport =
        MatchReport(
            id = from._id,
            category = from.category,
            city = from.city,
            competition = from.competition,
            createdAt = from.createdAt,
            division = from.division,
            hall = from.hall,
            matchId = from.matchId,
            matchNumber = from.matchNumber,
            officials = from.officials.toOfficials(),
            phase = from.phase,
            remarks = from.remarks,
            commissionerRemarks = from.commissionerRemarks,
            scout = from.scout.toScout(),
            scoutData = from.scoutData.map { it.map { it.toScoutData() } },
            settings = from.settings.toSettings(),
            spectators = from.spectators,
            startDate = from.startDate,
            teams = from.teams.toTeams(),
            updatedAt = from.updatedAt,
        )

    private fun TeamsResponse.toTeams(): Teams =
        Teams(
            away = away.toMatchTeam(),
            home = home.toMatchTeam(),
        )

    private fun TeamResponse.toMatchTeam(): MatchTeam =
        MatchTeam(
            captain = captain,
            code = code,
            libero = libero,
            name = name,
            players = players.map { it.toTeamPlayer() },
            shortName = shortName,
            staff = staff.toStaff()
        )

    private fun PlayerResponse.toTeamPlayer(): TeamPlayer =
        TeamPlayer(
            code = code,
            firstName = firstName,
            isForeign = isForeign,
            lastName = lastName,
            shirtNumber = shirtNumber,
        )

    private fun StaffResponse.toStaff(): Staff =
        Staff(
            assistant1 = assistant1?.toAssistant(),
            assistant2 = assistant2?.toAssistant(),
            coach = coach.toCoach(),
            medical1 = medical1?.toMedical(),
            medical2 = medical2?.toMedical(),
        )

    private fun MedicalResponse.toMedical(): Medical =
        Medical(
            firstName = firstName,
            lastName = lastName,
            type = type,
        )

    private fun AssistantResponse.toAssistant(): Assistant =
        Assistant(
            firstName = firstName,
            lastName = lastName,
        )

    private fun CoachResponse.toCoach(): Coach =
        Coach(
            firstName = firstName,
            lastName = lastName,
        )

    private fun OfficialsResponse.toOfficials(): Officials =
        Officials(
            commissioner = commissioner.toCommissioner(),
            referee1 = referee1.toReferee(),
            referee2 = referee2.toReferee(),
            scorer1 = scorer1.toScorer(),
            supervisor = supervisor?.toSupervisor(),
            lineJudge1 = lineJudge1?.toLineJudge(),
            lineJudge2 = lineJudge2?.toLineJudge(),
        )

    private fun CommissionerResponse.toCommissioner(): Commissioner =
        Commissioner(
            firstName = firstName,
            lastName = lastName,
        )

    private fun RefereeResponse.toReferee(): Referee =
        Referee(
            firstName = firstName,
            lastName = lastName,
            level = level,
        )

    private fun ScorerResponse.toScorer(): Scorer =
        Scorer(
            firstName = firstName,
            lastName = lastName,
            level = level,
        )

    private fun SupervisorResponse.toSupervisor(): Supervisor =
        Supervisor(
            firstName = firstName,
            lastName = lastName,
        )

    private fun LineJudgeResponse.toLineJudge(): LineJudge =
        LineJudge(
            firstName = firstName,
            lastName = lastName,
        )

    private fun ScoutResponse.toScout(): Scout =
        Scout(
            bestPlayer = bestPlayer?.toBestPlayer(),
            coinToss = coinToss.toCoinToss(),
            ended = ended,
            mvp = mvp.toMvp(),
            sets = sets.map { it.toSet() },
        )

    private fun BestPlayerResponse.toBestPlayer(): BestPlayer =
        BestPlayer(
            number = number,
            team = team,
        )

    private fun CoinTossResponse.toCoinToss(): CoinToss =
        CoinToss(
            start = start.toStart(),
            deciding = deciding?.toDeciding(),
        )

    private fun StartResponse.toStart(): Start =
        Start(
            leftSide = leftSide,
            serve = serve,
        )

    private fun DecidingResponse.toDeciding(): Deciding =
        Deciding(
            leftSide = leftSide,
            serve = serve,
        )

    private fun MvpResponse.toMvp(): Mvp =
        Mvp(
            number = number,
            team = team,
        )

    private fun SetResponse.toSet(): Set =
        Set(
            duration = duration,
            endTime = endTime,
            events = events.map { it.toEvent() },
            score = score.toScore(),
            startTime = startTime,
            startingLineup = startingLineup.toStartingLineup(),
        )

    private fun EventResponse.toEvent(): Event =
        libero?.toLibero() ?: rally?.toRally() ?: sanction?.toSanction() ?: improperRequest?.toImproperRequest() ?:
        delay?.toDelay() ?: injury?.toInjury() ?: newLibero?.toNewLibero() ?: substitution?.toSubstitution() ?:
        timeout?.toTimeout() ?: videoChallenge?.toVideoChallange() ?: error("Unexpected type of Event: $this")

    private fun LiberoResponse.toLibero(): Event.Libero =
        Event.Libero(
            enters = enters,
            libero = libero,
            player = player,
            team = team,
            time = time,
        )

    private fun RallyResponse.toRally(): Event.Rally =
        Event.Rally(
            endTime = endTime,
            point = point,
            startTime = startTime,
            verified = verified,
        )

    private fun SubstitutionResponse.toSubstitution(): Event.Substitution =
        Event.Substitution(
            `in` = `in`,
            out = out,
            team = team,
            time = time,
        )

    private fun TimeoutResponse.toTimeout(): Event.Timeout =
        Event.Timeout(
            team = team,
            time = time,
        )

    private fun VideoChallengeResponse.toVideoChallange(): Event.VideoChallenge =
        Event.VideoChallenge(
            atScore = atScore.toAtScore(),
            endTime = endTime,
            reason = reason,
            response = response,
            scoreChange = scoreChange,
            startTime = startTime,
            team = team,
        )

    private fun AtScoreResponse.toAtScore(): AtScore =
        AtScore(
            away = away,
            home = home,
        )

    private fun SanctionResponse.toSanction(): Event.Sanction =
        Event.Sanction(
            team = team,
            type = type,
            player = player,
            time = time,
            staff = staff,
        )

    private fun ImproperRequestResponse.toImproperRequest(): Event.ImproperRequest =
        Event.ImproperRequest(
            team = team,
            time = time,
        )

    private fun DelayResponse.toDelay(): Event.Delay =
        Event.Delay(
            team = team,
            time = time,
        )

    private fun InjuryResponse.toInjury(): Event.Injury =
        Event.Injury(
            team = team,
            player = player,
            time = time,
            libero = libero,
        )

    private fun NewLiberoResponse.toNewLibero(): Event.NewLibero =
        Event.NewLibero(
            team = team,
            player = player,
            time = time,
        )

    private fun ScoreResponse.toScore(): Score =
        Score(
            away = away,
            home = home,
        )

    private fun StartingLineupResponse.toStartingLineup(): StartingLineup =
        StartingLineup(
            away = away,
            home = home,
        )

    private fun SettingsResponse.toSettings(): Settings =
        Settings(
            decidingSetWin = decidingSetWin,
            regularSetWin = regularSetWin,
            winningScore = winningScore,
        )

    private fun ScoutDataResponse.toScoutData(): ScoutData =
        ScoutData(
            id = _id,
            plays = plays.map { it.toPlay() },
            point = point,
            score = score.toScore(),
        )

    private fun PlayResponse.toPlay(): Play =
        Play(
            id = _id,
            effect = effect,
            player = player,
            skill = skill,
            team = team,
        )
}