package com.kamilh.match_analyzer

import com.kamilh.extensions.atPolandOffset
import com.kamilh.extensions.divideExcluding
import com.kamilh.interactors.Interactor
import com.kamilh.match_analyzer.strategies.PlayActionStrategy
import com.kamilh.models.*
import com.kamilh.storage.TeamStorage
import utils.Logger
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

typealias MatchReportAnalyzer = Interactor<MatchReportAnalyzerParams, MatchReportAnalyzerResult>

typealias MatchReportAnalyzerResult = Result<MatchStatistics, MatchReportAnalyzerError>

data class MatchReportAnalyzerParams(
    val matchReport: MatchReport,
    val tour: TourYear,
    val league: League = League.POLISH_LEAGUE,
)

sealed class MatchReportAnalyzerError(override val message: String? = null) : Error {
    data class WrongSetsCount(val matchReportId: MatchReportId) : MatchReportAnalyzerError()
    data class TeamNotFound(
        val matchReportId: MatchReportId,
        val teamName: String,
        val tour: TourYear,
    ) : MatchReportAnalyzerError()
}

class MatchReportAnalyzerInteractor(
    appDispatchers: AppDispatchers,
    private val teamStorage: TeamStorage,
    private val strategies: List<PlayActionStrategy<*>>,
    private val preparer: EventsPreparer,
    private val analyzeErrorReporter: AnalyzeErrorReporter,
) : MatchReportAnalyzer(appDispatchers) {

    override suspend fun doWork(params: MatchReportAnalyzerParams): MatchReportAnalyzerResult =
        analyze(params.matchReport, params.tour, params.league)

    suspend fun analyze(matchReport: MatchReport, tour: TourYear, league: League = League.POLISH_LEAGUE): MatchReportAnalyzerResult {
        val matchId = matchReport.matchId

        if (matchReport.scout.sets.size > matchReport.scoutData.size) {
            return Result.failure(MatchReportAnalyzerError.WrongSetsCount(matchId))
        }

        val home = getTeam(matchReport.matchTeams.home, league, tour)
        val away = getTeam(matchReport.matchTeams.away, league, tour)

        if (home == null) {
            return Result.failure(MatchReportAnalyzerError.TeamNotFound(matchId, matchReport.matchTeams.home.name, tour))
        }

        if (away == null) {
            return Result.failure(MatchReportAnalyzerError.TeamNotFound(matchId, matchReport.matchTeams.away.name, tour))
        }

        val toTeam: TeamType.() -> Team = {
            when (this) {
                TeamType.Home -> home
                TeamType.Away -> away
            }
        }

        val sets = matchReport.scout.sets
        val scoutData = matchReport.scoutData

        val matchSets = sets.mapIndexed { setIndex, set ->
            val homeLineup = set.startingLineup.homeLineupMutator(matchId, matchReport.matchTeams, Rotation.P1)
            val awayLineup = set.startingLineup.awayLineupMutator(matchId, matchReport.matchTeams, Rotation.P1)
            val setScoutData = scoutData[setIndex]

            var score = Score(home = 0, away = 0)
            var lastPoint: TeamType? = setScoutData.first().plays.first { it.skill == Skill.Serve }.team
            val points = mutableListOf<MatchPoint>()
            val sortedEvents = preparer.prepare(set.events)
            sortedEvents.forEachIndexed { index, event ->
                when (event) {
                    is Event.Rally -> {
                        var point = event.point
                        sortedEvents
                            .divideExcluding(index).after
                            .takeWhile { it !is Event.Rally }
                            .filterIsInstance<Event.VideoChallenge>()
                            .forEach { videoChallenge ->
                                when (videoChallenge.scoreChange) {
                                    Event.VideoChallenge.ScoreChange.AssignToOther -> point = point?.other
                                    Event.VideoChallenge.ScoreChange.RepeatLast -> point = null
                                    Event.VideoChallenge.ScoreChange.NoChange, null -> { }
                                }
                            }
                        point?.let { team ->
                            score = score.increment(team)
                            val dataToAnalyze = setScoutData.firstOrNull { it.score == score }
                            val data = dataToAnalyze?.copy(plays = dataToAnalyze.plays.filter { it.player != 0 })

                            if (data == null || data.plays.isEmpty()) {
                                analyzeErrorReporter.report(AnalyzeError.NoScoutDataForScore(matchId, score, setIndex + 1, tour))
                            } else if (data.plays.firstOrNull()?.skill != Skill.Serve) {
                                analyzeErrorReporter.report(AnalyzeError.ActionStartNotFromService(matchId, score, setIndex + 1))
                            } else {
                                val input = AnalysisInput(
                                    plays = data.plays.mapNotNull { play ->
                                        val player = when (play.team) {
                                            TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchId, play.player)
                                            TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchId, play.player)
                                        } ?: return@mapNotNull null
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
                                                if (play.skill == Skill.Block) {
                                                    when (this) {
                                                        PlayerPosition.P2, PlayerPosition.P3, PlayerPosition.P4 -> { }
                                                        PlayerPosition.P1, PlayerPosition.P5, PlayerPosition.P6, null -> {
                                                            analyzeErrorReporter.report(
                                                                AnalyzeError.BlockNotInFirstLine(
                                                                    matchId, play.team.toTeam(), tour, play.player,
                                                                    this, player, score, setIndex + 1, event.startTime
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                if (this == null) {
                                                    analyzeErrorReporter.report(
                                                        AnalyzeError.PlayerNotInGame(matchId, play.team.toTeam(), tour,
                                                            play.player, player, score, setIndex + 1, event.startTime
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    },
                                    matchId = matchId,
                                    set = setIndex + 1,
                                    score = score,
                                    rallyStartTime = event.startTime,
                                    rallyEndTime = event.endTime,
                                )
                                points.add(
                                    MatchPoint(
                                        score = score,
                                        startTime = event.startTime.atPolandOffset(),
                                        endTime = event.endTime.atPolandOffset(),
                                        playActions = strategies.flatMap { it.check(input) },
                                        point = when (team) {
                                            TeamType.Home -> home.id
                                            TeamType.Away -> away.id
                                        },
                                        homeLineup = homeLineup.currentLineup,
                                        awayLineup = awayLineup.currentLineup,
                                    )
                                )
                            }

                            val last = lastPoint
                            if (last != null && last != point) {
                                if (last == TeamType.Home && point == TeamType.Away ) {
                                    awayLineup.rotate()
                                } else {
                                    homeLineup.rotate()
                                }
                            }
                            lastPoint = point
                        }
                    }
                    is Event.Libero -> {
                        when (event.team) {
                            TeamType.Home -> homeLineup.libero(matchId, event, matchReport.matchTeams.home)
                            TeamType.Away -> awayLineup.libero(matchId, event, matchReport.matchTeams.away)
                        }.apply {
                            if (!this) {
                                analyzeErrorReporter.report(
                                    AnalyzeError.WrongLibero(matchId, score, setIndex + 1, event, event.team.toTeam())
                                )
                            }
                        }
                    }
                    is Event.Substitution -> {
                        when (event.team) {
                            TeamType.Home -> homeLineup.substitution(matchId, event, matchReport.matchTeams.home)
                            TeamType.Away -> awayLineup.substitution(matchId, event, matchReport.matchTeams.away)
                        }.apply {
                            if (!this) {
                                analyzeErrorReporter.report(
                                    AnalyzeError.WrongSubstitution(matchId, score, setIndex + 1, event, event.team.toTeam())
                                )
                            }
                        }
                    }
                    is Event.ManualChange -> {
                        Logger.i("ManualChange EventScore: ${event.score}, CurrentScore: $score")
                    }
                }
            }
            if (score != set.score) {
                analyzeErrorReporter.report(
                    AnalyzeError.CalculatedScoreDifferentThanExpected(matchId, calculatedScore = score, expectedScore = set.score, tour)
                )
            }
            MatchSet(
                number = setIndex + 1,
                score = score,
                points = points,
                startTime = set.startTime.atPolandOffset(),
                endTime = set.endTime.atPolandOffset(),
                duration = set.duration.toDuration(DurationUnit.MINUTES),
            )
        }

        return Result.success(
            MatchStatistics(
                matchReportId = matchId,
                sets = matchSets,
                home = MatchTeam(
                    teamId = home.id,
                    code = matchReport.matchTeams.home.code,
                    players = matchReport.matchTeams.home.players.map { it.toMatchPlayer() },
                ),
                away = MatchTeam(
                    teamId = away.id,
                    code = matchReport.matchTeams.away.code,
                    players = matchReport.matchTeams.away.players.map { it.toMatchPlayer() },
                ),
                mvp = when (matchReport.scout.mvp.team) {
                    TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchId, matchReport.scout.mvp.number)
                    TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchId, matchReport.scout.mvp.number)
                }!!,
                bestPlayer = when (matchReport.scout.bestPlayer?.team) {
                    TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchId, matchReport.scout.bestPlayer.number)
                    TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchId, matchReport.scout.bestPlayer.number)
                    else -> null
                },
                phase = matchReport.phase,
                updatedAt = LocalDateTime.now(),
            )
        )
    }

    private suspend fun getTeam(matchTeam: MatchReportTeam, league: League, tour: TourYear): Team? =
        teamStorage.getTeam(matchTeam.name, matchTeam.code, league, tour)

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
            substitution(`in` = second, out = first)
            true
        } else if (!contains(first) && contains(second)) {
            substitution(`in` = first, out = second)
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

    private fun MatchReportPlayer.toMatchPlayer(): MatchPlayer =
        MatchPlayer(
            id = id,
            firstName = firstName,
            isForeign = isForeign,
            lastName = lastName,
        )
}