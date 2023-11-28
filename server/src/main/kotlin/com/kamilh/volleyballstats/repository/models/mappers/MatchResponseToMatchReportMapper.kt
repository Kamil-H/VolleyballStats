package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.models.AtScore
import com.kamilh.volleyballstats.models.BestPlayer
import com.kamilh.volleyballstats.models.Event
import com.kamilh.volleyballstats.models.MatchReportId
import com.kamilh.volleyballstats.models.MatchReportPlayer
import com.kamilh.volleyballstats.models.MatchReportTeam
import com.kamilh.volleyballstats.models.MatchScore
import com.kamilh.volleyballstats.models.MatchTeams
import com.kamilh.volleyballstats.models.Mvp
import com.kamilh.volleyballstats.models.Play
import com.kamilh.volleyballstats.models.RawMatchReport
import com.kamilh.volleyballstats.models.Scout
import com.kamilh.volleyballstats.models.ScoutData
import com.kamilh.volleyballstats.models.Set
import com.kamilh.volleyballstats.models.StartingLineup
import com.kamilh.volleyballstats.models.TeamType
import com.kamilh.volleyballstats.repository.models.AtScoreResponse
import com.kamilh.volleyballstats.repository.models.BestPlayerResponse
import com.kamilh.volleyballstats.repository.models.DelayResponse
import com.kamilh.volleyballstats.repository.models.EventResponse
import com.kamilh.volleyballstats.repository.models.ImproperRequestResponse
import com.kamilh.volleyballstats.repository.models.InjuryResponse
import com.kamilh.volleyballstats.repository.models.LiberoResponse
import com.kamilh.volleyballstats.repository.models.ManualChangeResponse
import com.kamilh.volleyballstats.repository.models.MatchResponse
import com.kamilh.volleyballstats.repository.models.MvpResponse
import com.kamilh.volleyballstats.repository.models.NewLiberoResponse
import com.kamilh.volleyballstats.repository.models.PlayResponse
import com.kamilh.volleyballstats.repository.models.PlayerResponse
import com.kamilh.volleyballstats.repository.models.RallyResponse
import com.kamilh.volleyballstats.repository.models.SanctionResponse
import com.kamilh.volleyballstats.repository.models.ScoreResponse
import com.kamilh.volleyballstats.repository.models.ScoutDataResponse
import com.kamilh.volleyballstats.repository.models.ScoutResponse
import com.kamilh.volleyballstats.repository.models.SetResponse
import com.kamilh.volleyballstats.repository.models.StartingLineupResponse
import com.kamilh.volleyballstats.repository.models.SubstitutionResponse
import com.kamilh.volleyballstats.repository.models.TeamResponse
import com.kamilh.volleyballstats.repository.models.TeamsResponse
import com.kamilh.volleyballstats.repository.models.TimeoutResponse
import com.kamilh.volleyballstats.repository.models.VideoChallengeResponse
import me.tatarka.inject.annotations.Inject
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

@Inject
class MatchResponseToMatchReportMapper {

    fun map(from: MatchResponse): RawMatchReport =
        RawMatchReport(
            matchId = MatchReportId(from.matchId.toLong()),
            phase = when {
                PHASE_REGULAR_SEASON.contains(from.phase.trim()) -> Phase.RegularSeason
                PHASE_PLAYOFF.contains(from.phase.trim()) -> Phase.PlayOff
                else -> error("Wrong Phase: ${from.phase}")
            },
            scout = from.scout.toScout(),
            scoutData = from.scoutData.map { it.map { it.toScoutData() } },
            matchTeams = from.teams.toTeams(),
        )

    private fun TeamsResponse.toTeams(): MatchTeams =
        MatchTeams(
            away = away.toMatchTeam(),
            home = home.toMatchTeam(),
        )

    private fun TeamResponse.toMatchTeam(): MatchReportTeam =
        MatchReportTeam(
            code = code,
            libero = libero,
            name = name,
            players = players.mapNotNull { it.toTeamPlayer() },
        )

    private fun PlayerResponse.toTeamPlayer(): MatchReportPlayer? =
        code.toLongOrNull()?.let {
            MatchReportPlayer(
                id = PlayerId(it),
                firstName = firstName,
                isForeign = isForeign,
                lastName = lastName,
                shirtNumber = shirtNumber,
            )
        }

    private fun ScoutResponse.toScout(): Scout =
        Scout(
            bestPlayer = bestPlayer?.toBestPlayer(),
            ended = ended,
            mvp = mvp.toMvp(),
            sets = sets.map { it.toSet() },
        )

    private fun BestPlayerResponse.toBestPlayer(): BestPlayer =
        BestPlayer(
            number = number,
            team = team.toTeamType(),
        )

    private fun MvpResponse.toMvp(): Mvp =
        Mvp(
            number = number,
            team = team.toTeamType(),
        )

    private fun SetResponse.toSet(): Set =
        Set(
            duration = duration ?: DEFAULT_SET_DURATION,
            endTime = endTime ?: startTime.plus(DEFAULT_SET_DURATION.minutes),
            events = events.flatMap { it.toEvent() },
            matchScore = score?.toScore(),
            startTime = startTime,
            startingLineup = startingLineup.toStartingLineup(),
        )

    private fun EventResponse.toEvent(): List<Event> =
        listOfNotNull(
            libero?.toLibero(),
            rally?.toRally(),
            sanction?.toSanction(),
            improperRequest?.toImproperRequest(),
            delay?.toDelay(),
            injury?.toInjury(),
            newLibero?.toNewLibero(),
            substitution?.toSubstitution(),
            timeout?.toTimeout(),
            videoChallenge?.toVideoChallange(),
            manualChange?.toManualChange(),
        ).sortedBy { it.time }

    private fun LiberoResponse.toLibero(): Event.Libero =
        Event.Libero(
            enters = enters,
            libero = libero,
            player = player,
            team = team.toTeamType(),
            time = time,
        )

    private fun RallyResponse.toRally(): Event.Rally =
        Event.Rally(
            endTime = endTime ?: startTime,
            point = point?.toTeamTypeOrNull(),
            startTime = startTime,
        )

    private fun SubstitutionResponse.toSubstitution(): Event.Substitution =
        Event.Substitution(
            `in` = `in`,
            out = out,
            team = team.toTeamType(),
            time = time,
        )

    private fun TimeoutResponse.toTimeout(): Event.Timeout =
        Event.Timeout(
            team = team.toTeamType(),
            time = time,
        )

    private fun VideoChallengeResponse.toVideoChallange(): Event.VideoChallenge =
        Event.VideoChallenge(
            atScore = atScore.toAtScore(),
            endTime = endTime,
            reason = reason,
            response = when (response) {
                VIDEO_CHALLENGE_RESPONSE_RIGHT -> Event.VideoChallenge.Response.Right
                VIDEO_CHALLENGE_RESPONSE_WRONG -> Event.VideoChallenge.Response.Wrong
                VIDEO_CHALLENGE_RESPONSE_INCONCLUSIVE -> Event.VideoChallenge.Response.Inconclusive
                else -> null
            },
            scoreChange = when (scoreChange) {
                VIDEO_CHALLENGE_SCORE_CHANGE_ASSIGN_TO_OTHER -> Event.VideoChallenge.ScoreChange.AssignToOther
                VIDEO_CHALLENGE_SCORE_CHANGE_NO_CHANGE -> Event.VideoChallenge.ScoreChange.NoChange
                VIDEO_CHALLENGE_SCORE_CHANGE_REPEAT_LAST -> Event.VideoChallenge.ScoreChange.RepeatLast
                else -> null
            },
            startTime = startTime,
            team = team.toTeamType(),
        )

    private fun ManualChangeResponse.toManualChange(): Event.ManualChange =
        Event.ManualChange(
            matchScore = score.toScore(),
            lineup = lineup.toStartingLineup(),
            serve = serve.toTeamType(),
            time = null,
        )

    private fun AtScoreResponse.toAtScore(): AtScore =
        AtScore(
            away = away,
            home = home,
        )

    private fun SanctionResponse.toSanction(): Event.Sanction =
        Event.Sanction(
            team = team.toTeamType(),
            type = type,
            player = player,
            time = time,
            staff = staff,
        )

    private fun ImproperRequestResponse.toImproperRequest(): Event.ImproperRequest =
        Event.ImproperRequest(
            team = team.toTeamType(),
            time = time,
        )

    private fun DelayResponse.toDelay(): Event.Delay =
        Event.Delay(
            team = team.toTeamType(),
            time = time,
        )

    private fun InjuryResponse.toInjury(): Event.Injury =
        Event.Injury(
            team = team.toTeamType(),
            player = player,
            time = time,
            libero = libero,
        )

    private fun NewLiberoResponse.toNewLibero(): Event.NewLibero =
        Event.NewLibero(
            team = team.toTeamType(),
            player = player,
            time = time,
        )

    private fun ScoreResponse.toScore(): MatchScore =
        MatchScore(
            away = away,
            home = home,
        )

    private fun StartingLineupResponse.toStartingLineup(): StartingLineup =
        StartingLineup(
            away = away,
            home = home,
        )

    private fun ScoutDataResponse.toScoutData(): ScoutData =
        ScoutData(
            id = _id,
            plays = plays.map { it.toPlay() },
            point = point.toTeamType(),
            matchScore = score.toScore(),
        )

    private fun PlayResponse.toPlay(): Play =
        Play(
            id = _id ?: UUID.randomUUID().toString(),
            effect = effect.toEffect(),
            player = player,
            skill = skill.toSkill(),
            team = team.toTeamType(),
        )

    private fun Char.toEffect(): Effect =
        when (this) {
            EFFECT_PERFECT -> Effect.Perfect
            EFFECT_POSITIVE -> Effect.Positive
            EFFECT_NEGATIVE -> Effect.Negative
            EFFECT_ERROR -> Effect.Error
            EFFECT_HALF -> Effect.Half
            EFFECT_INVASION -> Effect.Invasion
            else -> error("Wrong Effect: $this")
        }

    private fun Char.toSkill(): Skill =
        when (this) {
            SKILL_ATTACK -> Skill.Attack
            SKILL_BLOCK -> Skill.Block
            SKILL_DIG -> Skill.Dig
            SKILL_SET -> Skill.Set
            SKILL_FREEBALL -> Skill.Freeball
            SKILL_RECEIVE -> Skill.Receive
            SKILL_SERVE -> Skill.Serve
            else -> error("Wrong Skill: $this")
        }

    private fun String.toTeamType(): TeamType =
        toTeamTypeOrNull() ?: error("Wrong TeamType: $this")

    private fun String.toTeamTypeOrNull(): TeamType? =
        when (this) {
            TEAM_TYPE_AWAY -> TeamType.Away
            TEAM_TYPE_HOME -> TeamType.Home
            else -> null
        }

    companion object {
        private const val EFFECT_PERFECT = '#'
        private const val EFFECT_POSITIVE = '+'
        private const val EFFECT_NEGATIVE = '-'
        private const val EFFECT_ERROR = '='
        private const val EFFECT_HALF = '/'
        private const val EFFECT_INVASION = '!'

        private const val TEAM_TYPE_HOME = "home"
        private const val TEAM_TYPE_AWAY = "away"

        private const val SKILL_ATTACK = 'A'
        private const val SKILL_BLOCK = 'B'
        private const val SKILL_DIG = 'D'
        private const val SKILL_SET = 'E'
        private const val SKILL_FREEBALL = 'F'
        private const val SKILL_RECEIVE = 'R'
        private const val SKILL_SERVE = 'S'

        private const val VIDEO_CHALLENGE_RESPONSE_RIGHT = "right"
        private const val VIDEO_CHALLENGE_RESPONSE_WRONG = "wrong"
        private const val VIDEO_CHALLENGE_RESPONSE_INCONCLUSIVE = "inconclusive"

        private const val VIDEO_CHALLENGE_SCORE_CHANGE_ASSIGN_TO_OTHER = "assignToOther"
        private const val VIDEO_CHALLENGE_SCORE_CHANGE_REPEAT_LAST = "repeatLast"
        private const val VIDEO_CHALLENGE_SCORE_CHANGE_NO_CHANGE = "noChange"

        private val PHASE_PLAYOFF = listOf(
            "Play Off", "Finał", "Play Off - 1/4 finału", "PLAY OFF - 1/4 finału", "PO", "PLAY OFF  - 1/4 finału",
            "o 11-12 miejsce", "Play Off - o 5-6 miejsce", "Play off - 1/4 finału", "PLAY OFF - 1/2 finału", "o 9-10 miejsce",
            "o 11--12 miejsce", "PLAY OFF  - o miejsca 1-2", "PLAY OFF  - 1/2 finału", "Play Off 1/4 finału", "Mecz o 1-2 miejsce",
            "PLAY -OFF o miejsca 3-4", "PLAY-OFF,  o miejsca 3-4", "O miejsce 5-6", "Play Off - o 9-10 miejsce", "Play- Off - 1/2 Finału",
            "Play Off o 3 -- 4 miejsce", "Play off o miejsce 5", "O 3. miejsce", "Baraż", "Play-out", "Baraż o prawo gry w PlusLidze w sezonie 2022/2023",
            "Play Out - Baraż", "PLAY-OFF", "Play off - 1/2 finału", "PLAY OFF  - finał", "o 3. miejsce", "Play off - o 3 miejsce", "o 13-14 miejsce",
            "o 9--10 miejsce w klasyfikacji końcowej", "MECZ O 5 MIEJSCE", "Play off - o 7-8 miejsce"
        )
        private val PHASE_REGULAR_SEASON = listOf("FZ", "ZAS", "Faza Zasadnicza")

        private const val DEFAULT_SET_DURATION = 25
    }
}
