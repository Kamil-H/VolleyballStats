package com.kamilh.volleyballstats.matchanalyzer

import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.extensions.divideExcluding
import com.kamilh.volleyballstats.matchanalyzer.strategies.PlayActionStrategy
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.models.Set
import com.kamilh.volleyballstats.storage.TeamStorage
import me.tatarka.inject.annotations.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

typealias MatchReportAnalyzer = Interactor<MatchReportAnalyzerParams, MatchReportAnalyzerResult>

typealias MatchReportAnalyzerResult = Result<MatchReport, MatchReportAnalyzerError>

data class MatchReportAnalyzerParams(
    val matchId: MatchId,
    val matchReport: RawMatchReport,
    val tour: Tour,
)

sealed class MatchReportAnalyzerError(override val message: String) : Error {
    class WrongSetsCount(val matchReportId: MatchReportId) : MatchReportAnalyzerError(
        "WrongSetsCount(matchReportId: $matchReportId)"
    )
    class TeamNotFound(
        val matchReportId: MatchReportId,
        val teamName: String,
        val season: Season,
    ) : MatchReportAnalyzerError(
        "TeamNotFound(matchReportId: $matchReportId, teamName: $teamName, season: $season)"
    )
}

@Inject
@Suppress("LargeClass")
class MatchReportAnalyzerInteractor(
    appDispatchers: AppDispatchers,
    private val teamStorage: TeamStorage,
    private val strategies: List<PlayActionStrategy<*>>,
    private val preparer: EventsPreparer,
    private val analyzeErrorReporter: AnalyzeErrorReporter,
) : MatchReportAnalyzer(appDispatchers) {

    override suspend fun doWork(params: MatchReportAnalyzerParams): MatchReportAnalyzerResult {
        val (matchId: MatchId, matchReport: RawMatchReport, tour: Tour) = params
        val matchReportId = matchReport.matchId
        val home = getTeam(matchReport.matchTeams.home, tour)
        val away = getTeam(matchReport.matchTeams.away, tour)
        return when {
            matchReport.scout.sets.size > matchReport.scoutData.size ->
                Result.failure(MatchReportAnalyzerError.WrongSetsCount(matchReportId))
            home == null ->
                Result.failure(MatchReportAnalyzerError.TeamNotFound(matchReportId, matchReport.matchTeams.home.name, tour.season))
            away == null ->
                Result.failure(MatchReportAnalyzerError.TeamNotFound(matchReportId, matchReport.matchTeams.away.name, tour.season))
            else -> Result.success(
                createMatchReport(
                    matchId = matchId,
                    matchReportId = matchReportId,
                    tour = tour,
                    matchReport = matchReport,
                    home = home,
                    away = away
                )
            )
        }
    }

    private fun createMatchReport(
        matchId: MatchId,
        matchReportId: MatchReportId,
        tour: Tour,
        matchReport: RawMatchReport,
        home: Team,
        away: Team,
    ): MatchReport = MatchReport(
        matchId = matchId,
        sets = createMatchSets(matchReportId = matchReportId, matchReport = matchReport, tour = tour,
            sets = matchReport.scout.sets, scoutData = matchReport.scoutData, home = home, away = away,
        ),
        home = MatchTeam(
            teamId = home.id,
            code = matchReport.matchTeams.home.code,
            players = matchReport.matchTeams.home.players.map { it.id },
        ),
        away = MatchTeam(
            teamId = away.id,
            code = matchReport.matchTeams.away.code,
            players = matchReport.matchTeams.away.players.map { it.id },
        ),
        mvp = when (matchReport.scout.mvp.team) {
            TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchReportId, matchReport.scout.mvp.number)
            TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchReportId, matchReport.scout.mvp.number)
        }!!,
        bestPlayer = when (matchReport.scout.bestPlayer?.team) {
            TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchReportId, matchReport.scout.bestPlayer.number)
            TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchReportId, matchReport.scout.bestPlayer.number)
            else -> null
        },
        phase = matchReport.phase,
        updatedAt = CurrentDate.localDateTime,
    )

    private fun createMatchSets(
        matchReportId: MatchReportId,
        matchReport: RawMatchReport,
        tour: Tour,
        sets: List<Set>,
        scoutData: List<List<ScoutData>>,
        home: Team,
        away: Team,
    ): List<MatchSet> =
        sets.mapIndexedNotNull { setIndex, set ->
            val setScoutData = scoutData[setIndex]
            if (setScoutData.isEmpty()) {
                analyzeErrorReporter.report(
                    AnalyzeError.NoSetScoutData(matchReportId = matchReportId, set = setIndex + 1)
                )
                null
            } else {
                val score = Score(home = 0, away = 0)
                AnalyzeScope(
                    matchReportId = matchReportId,
                    matchReport = matchReport,
                    setScoutData = setScoutData,
                    setIndex = setIndex,
                    tour = tour,
                    set = set,
                    home = home,
                    away = away,
                    homeLineup = set.startingLineup.homeLineupMutator(matchReportId, matchReport.matchTeams, Rotation.P1),
                    awayLineup = set.startingLineup.awayLineupMutator(matchReportId, matchReport.matchTeams, Rotation.P1),
                    score = score,
                    lastPoint = getSetStartTeam(score, setScoutData, matchReportId, setIndex),
                    points = mutableListOf(),
                    sortedEvents = preparer.prepare(set.events),
                ).createMatchSet()
            }
        }

    private fun getSetStartTeam(
        score: Score,
        setScoutData: List<ScoutData>,
        matchReportId: MatchReportId,
        setIndex: Int,
    ): TeamType {
        val plays = setScoutData.first().plays
        val serveTeam = plays.firstOrNull { it.skill == Skill.Serve }?.team
        return serveTeam ?: plays.first().team.also {
            analyzeErrorReporter.report(
                AnalyzeError.ActionStartNotFromService(
                    matchReportId = matchReportId,
                    score = score,
                    set = setIndex + 1,
                )
            )
        }
    }

    private fun AnalyzeScope.createMatchSet(): MatchSet {
        sortedEvents.forEachIndexed { index, event ->
            when (event) {
                is Event.Rally -> handleRally(event, index)
                is Event.Libero -> handleLibero(event)
                is Event.Substitution -> handleSubstitution(event)
                is Event.ManualChange -> handleManualChange(event)
                else -> { }
            }
        }
        validateScore()
        return MatchSet(
            number = setIndex + 1,
            score = score,
            points = points,
            startTime = set.startTime.atPolandZone(),
            endTime = set.endTime.atPolandZone(),
            duration = set.duration.toDuration(DurationUnit.MINUTES),
        )
    }

    private fun AnalyzeScope.handleRally(event: Event.Rally, index: Int) {
        getWhoScored(event, sortedEvents, index)?.let { team ->
            score = score.increment(team)
            val dataToAnalyze = setScoutData.firstOrNull {
                it.matchScore.away == score.away && it.matchScore.home == score.home
            }
            val data = dataToAnalyze?.copy(plays = dataToAnalyze.plays.filter { it.player != 0 })

            if (data == null || data.plays.isEmpty()) {
                analyzeErrorReporter.report(AnalyzeError.NoScoutDataForScore(matchReportId, score, setIndex + 1, tour.season))
            } else if (data.plays.firstOrNull()?.skill != Skill.Serve) {
                analyzeErrorReporter.report(AnalyzeError.ActionStartNotFromService(matchReportId, score, setIndex + 1))
            } else {
                val input = createInput(data, event)
                points.add(createPoint(score, event, team, input))
            }
            val last = lastPoint
            if (last != team) {
                if (last == TeamType.Home && team == TeamType.Away) {
                    awayLineup.rotate()
                } else {
                    homeLineup.rotate()
                }
            }
            lastPoint = team
        }
    }

    private fun getWhoScored(
        event: Event.Rally,
        sortedEvents: List<Event>,
        index: Int,
    ): TeamType? {
        var point = event.point
        sortedEvents.divideExcluding(index).after
            .takeWhile { it !is Event.Rally }
            .filterIsInstance<Event.VideoChallenge>()
            .forEach { videoChallenge ->
                when (videoChallenge.scoreChange) {
                    Event.VideoChallenge.ScoreChange.AssignToOther -> point = point?.other
                    Event.VideoChallenge.ScoreChange.RepeatLast -> point = null
                    Event.VideoChallenge.ScoreChange.NoChange, null -> { }
                }
            }
        return point
    }

    private fun AnalyzeScope.handleLibero(event: Event.Libero) {
        val success = when (event.team) {
            TeamType.Home -> homeLineup.libero(matchReportId, event, matchReport.matchTeams.home)
            TeamType.Away -> awayLineup.libero(matchReportId, event, matchReport.matchTeams.away)
        }
        if (!success) {
            analyzeErrorReporter.report(
                AnalyzeError.WrongLibero(matchReportId, score, setIndex + 1, event, event.team.toTeam(this))
            )
        }
    }

    private fun AnalyzeScope.handleSubstitution(event: Event.Substitution) {
        val success = when (event.team) {
            TeamType.Home -> homeLineup.substitution(matchReportId, event, matchReport.matchTeams.home)
            TeamType.Away -> awayLineup.substitution(matchReportId, event, matchReport.matchTeams.away)
        }
        if (!success) {
            analyzeErrorReporter.report(
                AnalyzeError.WrongSubstitution(matchReportId, score, setIndex + 1, event, event.team.toTeam(this))
            )
        }
    }

    private fun AnalyzeScope.handleManualChange(event: Event.ManualChange) {
        Logger.i("ManualChange EventScore: ${event.matchScore}, CurrentScore: $score")
    }

    private fun AnalyzeScope.validateScore() {
        val matchScore = set.matchScore
        if (matchScore == null) {
            analyzeErrorReporter.report(
                AnalyzeError.NoScoreForSetData(matchReportId, setIndex + 1)
            )
        } else if (score.away != matchScore.away && score.home != matchScore.home) {
            analyzeErrorReporter.report(
                AnalyzeError.CalculatedScoreDifferentThanExpected(
                    matchReportId = matchReportId,
                    calculatedScore = score,
                    expectedScore = Score(home = matchScore.home, away = matchScore.away),
                    tour = tour.season
                )
            )
        }
    }

    private fun AnalyzeScope.createInput(data: ScoutData, event: Event.Rally): AnalysisInput =
        AnalysisInput(
            plays = data.plays.mapNotNull { play ->
                when (play.team) {
                    TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchReportId, play.player)
                    TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchReportId, play.player)
                }?.let {  player ->
                     createPlay(play, player, event)
                }
            },
        )

    private fun AnalyzeScope.createPlay(play: Play, player: PlayerId, event: Event.Rally): AnalysisInput.Play =
        AnalysisInput.Play(
            id = play.id,
            effect = play.effect,
            player = player,
            skill = play.skill,
            team = when (play.team) {
                TeamType.Home -> home.id
                TeamType.Away -> away.id
            },
            position = when (play.team) {
                TeamType.Home -> homeLineup.positionOrNull(player)
                TeamType.Away -> awayLineup.positionOrNull(player)
            }.apply {
                validatePlayerPosition(
                    playerPosition = this, event = event, player = player, score = score, play = play,
                )
            }
        )

    private fun AnalyzeScope.validatePlayerPosition(
        playerPosition: PlayerPosition?,
        event: Event.Rally,
        player: PlayerId,
        score: Score,
        play: Play,
    ) {
        if (play.skill == Skill.Block) {
            when (playerPosition) {
                PlayerPosition.P2, PlayerPosition.P3, PlayerPosition.P4 -> { }
                PlayerPosition.P1, PlayerPosition.P5, PlayerPosition.P6, null -> {
                    analyzeErrorReporter.report(
                        AnalyzeError.BlockNotInFirstLine(
                            matchReportId, play.team.toTeam(this), tour.season, play.player,
                            playerPosition, player, score, setIndex + 1, event.startTime
                        )
                    )
                }
            }
        }
        if (playerPosition == null) {
            analyzeErrorReporter.report(
                AnalyzeError.PlayerNotInGame(matchReportId, play.team.toTeam(this), tour.season,
                    play.player, player, score, setIndex + 1, event.startTime
                )
            )
        }
    }

    private fun AnalyzeScope.createPoint(score: Score, event: Event.Rally, team: TeamType, input: AnalysisInput): MatchPoint =
        MatchPoint(
            score = score,
            startTime = event.startTime.atPolandZone(),
            endTime = event.endTime.atPolandZone(),
            playActions = strategies.flatMap { it.check(input) },
            point = when (team) {
                TeamType.Home -> home.id
                TeamType.Away -> away.id
            },
            homeLineup = homeLineup.currentLineup,
            awayLineup = awayLineup.currentLineup,
        )

    private suspend fun getTeam(matchTeam: MatchReportTeam, tour: Tour): Team? =
        teamStorage.getTeam(matchTeam.name, matchTeam.code, tour.id)

    private fun LineupMutator.positionOrNull(playerId: PlayerId): PlayerPosition? =
        if (contains(playerId)) {
            position(playerId)
        } else {
            null
        }

    private fun LineupMutator.libero(matchReportId: MatchReportId, libero: Event.Libero, matchTeam: MatchReportTeam): Boolean =
        doSubstitution(first = matchTeam.playerIdOrNull(matchReportId, libero.libero), second = matchTeam.playerIdOrNull(matchReportId, libero.player))

    private fun LineupMutator.substitution(matchReportId: MatchReportId, substitution: Event.Substitution, matchTeam: MatchReportTeam): Boolean =
        doSubstitution(first = matchTeam.playerIdOrNull(matchReportId, substitution.`in`), second = matchTeam.playerIdOrNull(matchReportId, substitution.out))

    private fun LineupMutator.doSubstitution(first: PlayerId?, second: PlayerId?): Boolean {
        first ?: return false
        second ?: return false
        return if (contains(first) && !contains(second)) {
            substitution(inPlayer = second, outPlayer = first)
            true
        } else if (!contains(first) && contains(second)) {
            substitution(inPlayer = first, outPlayer = second)
            true
        } else {
            false
        }
    }

    private fun StartingLineup.homeLineupMutator(matchReportId: MatchReportId, matchTeams: MatchTeams, startingRotation: Rotation): LineupMutator =
        home.lineupMutator(matchReportId, matchTeams.home, startingRotation)

    private fun StartingLineup.awayLineupMutator(matchReportId: MatchReportId, matchTeams: MatchTeams, startingRotation: Rotation): LineupMutator =
        away.lineupMutator(matchReportId, matchTeams.away, startingRotation)

    private fun List<Int>.lineupMutator(matchReportId: MatchReportId, matchTeam: MatchReportTeam, startingRotation: Rotation): LineupMutator =
        LineupMutator(
            startingLineup = Lineup.from(mapNotNull { number -> matchTeam.playerIdOrNull(matchReportId, number) }),
            startingRotation = startingRotation,
        )

    private fun MatchReportTeam.playerIdOrNull(matchReportId: MatchReportId, shirtNumber: Int): PlayerId? =
        players.firstOrNull { it.shirtNumber == shirtNumber }?.id.apply {
            if (this == null) {
                analyzeErrorReporter.report(
                    AnalyzeError.PlayerNotFoundInTheTeam(matchReportId, name, shirtNumber)
                )
            }
        }

    private fun Score.increment(teamType: TeamType): Score =
        Score(
            home = if (teamType == TeamType.Home) home + 1 else home,
            away = if (teamType == TeamType.Away) away + 1 else away,
        )

    private val TeamType.other: TeamType
        get() = when (this) {
            TeamType.Home -> TeamType.Away
            TeamType.Away -> TeamType.Home
        }

    private fun TeamType.toTeam(analyzeScope: AnalyzeScope): Team =
        when (this) {
            TeamType.Home -> analyzeScope.home
            TeamType.Away -> analyzeScope.away
        }

    private class AnalyzeScope(
        val matchReportId: MatchReportId,
        val matchReport: RawMatchReport,
        val setScoutData: List<ScoutData>,
        val setIndex: Int,
        val tour: Tour,
        val set: Set,
        val home: Team,
        val away: Team,
        val homeLineup: LineupMutator,
        val awayLineup: LineupMutator,
        var score: Score,
        var lastPoint: TeamType?,
        val points: MutableList<MatchPoint>,
        val sortedEvents: List<Event>,
    )
}
