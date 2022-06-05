package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.models.Score
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.extensions.divideExcluding
import com.kamilh.volleyballstats.interactors.Interactor
import com.kamilh.volleyballstats.match_analyzer.strategies.PlayActionStrategy
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.domain.utils.Logger
import me.tatarka.inject.annotations.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

typealias MatchReportAnalyzer = Interactor<MatchReportAnalyzerParams, MatchReportAnalyzerResult>

typealias MatchReportAnalyzerResult = Result<MatchStatistics, MatchReportAnalyzerError>

data class MatchReportAnalyzerParams(
    val matchId: MatchId,
    val matchReport: MatchReport,
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
class MatchReportAnalyzerInteractor(
    appDispatchers: AppDispatchers,
    private val teamStorage: TeamStorage,
    private val strategies: List<PlayActionStrategy<*>>,
    private val preparer: EventsPreparer,
    private val analyzeErrorReporter: AnalyzeErrorReporter,
) : MatchReportAnalyzer(appDispatchers) {

    override suspend fun doWork(params: MatchReportAnalyzerParams): MatchReportAnalyzerResult {
        val (matchId: MatchId, matchReport: MatchReport, tour: Tour) = params
        val matchReportId = matchReport.matchId

        if (matchReport.scout.sets.size > matchReport.scoutData.size) {
            return Result.failure(MatchReportAnalyzerError.WrongSetsCount(matchReportId))
        }

        val home = getTeam(matchReport.matchTeams.home, tour)
        val away = getTeam(matchReport.matchTeams.away, tour)

        if (home == null) {
            return Result.failure(MatchReportAnalyzerError.TeamNotFound(matchReportId, matchReport.matchTeams.home.name, tour.season))
        }

        if (away == null) {
            return Result.failure(MatchReportAnalyzerError.TeamNotFound(matchReportId, matchReport.matchTeams.away.name, tour.season))
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
            val homeLineup = set.startingLineup.homeLineupMutator(matchReportId, matchReport.matchTeams, Rotation.P1)
            val awayLineup = set.startingLineup.awayLineupMutator(matchReportId, matchReport.matchTeams, Rotation.P1)
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
                            val dataToAnalyze = setScoutData.firstOrNull { it.score.away == score.away && it.score.home == score.home }
                            val data = dataToAnalyze?.copy(plays = dataToAnalyze.plays.filter { it.player != 0 })

                            if (data == null || data.plays.isEmpty()) {
                                analyzeErrorReporter.report(AnalyzeError.NoScoutDataForScore(matchReportId, score, setIndex + 1, tour.season))
                            } else if (data.plays.firstOrNull()?.skill != Skill.Serve) {
                                analyzeErrorReporter.report(AnalyzeError.ActionStartNotFromService(matchReportId, score, setIndex + 1))
                            } else {
                                val input = AnalysisInput(
                                    plays = data.plays.mapNotNull { play ->
                                        val player = when (play.team) {
                                            TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchReportId, play.player)
                                            TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchReportId, play.player)
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
                                                                    matchReportId, play.team.toTeam(), tour.season, play.player,
                                                                    this, player, score, setIndex + 1, event.startTime
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                if (this == null) {
                                                    analyzeErrorReporter.report(
                                                        AnalyzeError.PlayerNotInGame(matchReportId, play.team.toTeam(), tour.season,
                                                            play.player, player, score, setIndex + 1, event.startTime
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    },
                                )
                                points.add(
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
                            TeamType.Home -> homeLineup.libero(matchReportId, event, matchReport.matchTeams.home)
                            TeamType.Away -> awayLineup.libero(matchReportId, event, matchReport.matchTeams.away)
                        }.apply {
                            if (!this) {
                                analyzeErrorReporter.report(
                                    AnalyzeError.WrongLibero(matchReportId, score, setIndex + 1, event, event.team.toTeam())
                                )
                            }
                        }
                    }
                    is Event.Substitution -> {
                        when (event.team) {
                            TeamType.Home -> homeLineup.substitution(matchReportId, event, matchReport.matchTeams.home)
                            TeamType.Away -> awayLineup.substitution(matchReportId, event, matchReport.matchTeams.away)
                        }.apply {
                            if (!this) {
                                analyzeErrorReporter.report(
                                    AnalyzeError.WrongSubstitution(matchReportId, score, setIndex + 1, event, event.team.toTeam())
                                )
                            }
                        }
                    }
                    is Event.ManualChange -> {
                        Logger.i("ManualChange EventScore: ${event.score}, CurrentScore: $score")
                    }
                    else -> { }
                }
            }
            if (score.away != set.score.away && score.home != set.score.home) {
                analyzeErrorReporter.report(
                    AnalyzeError.CalculatedScoreDifferentThanExpected(matchReportId, calculatedScore = score, expectedScore = Score(home = set.score.home, away = set.score.away), tour.season)
                )
            }
            MatchSet(
                number = setIndex + 1,
                score = score,
                points = points,
                startTime = set.startTime.atPolandZone(),
                endTime = set.endTime.atPolandZone(),
                duration = set.duration.toDuration(DurationUnit.MINUTES),
            )
        }

        return Result.success(
            MatchStatistics(
                matchId = matchId,
                sets = matchSets,
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
                    TeamType.Home -> matchReport.matchTeams.home.playerIdOrNull(matchReportId, matchReport.scout.bestPlayer!!.number)
                    TeamType.Away -> matchReport.matchTeams.away.playerIdOrNull(matchReportId, matchReport.scout.bestPlayer!!.number)
                    else -> null
                },
                phase = matchReport.phase,
                updatedAt = CurrentDate.localDateTime,
            )
        )
    }

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
}