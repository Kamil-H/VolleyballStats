package com.kamilh.volleyballstats.matchanalyzer

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.models.Event
import com.kamilh.volleyballstats.models.MatchReportId
import me.tatarka.inject.annotations.Inject

sealed class AnalyzeError {

    data class PlayerNotFoundInTheTeam(
        val matchReportId: MatchReportId,
        val teamName: String,
        val shirtNumber: Int,
    ) : AnalyzeError()

    data class BlockNotInFirstLine(
        val matchReportId: MatchReportId,
        val team: Team,
        val tour: Season,
        val shirtNumber: Int,
        val position: PlayerPosition?,
        val playerId: PlayerId,
        val score: Score,
        val set: Int,
        val time: LocalDateTime,
    ) : AnalyzeError()

    data class PlayerNotInGame(
        val matchReportId: MatchReportId,
        val team: Team,
        val tour: Season,
        val shirtNumber: Int,
        val playerId: PlayerId,
        val score: Score,
        val set: Int,
        val time: LocalDateTime,
    ) : AnalyzeError()

    data class NoScoutDataForScore(
        val matchReportId: MatchReportId,
        val score: Score,
        val set: Int,
        val tour: Season,
    ) : AnalyzeError()

    data class CalculatedScoreDifferentThanExpected(
        val matchReportId: MatchReportId,
        val calculatedScore: Score,
        val expectedScore: Score,
        val tour: Season,
    ) : AnalyzeError()

    data class WrongSubstitution(
        val matchReportId: MatchReportId,
        val score: Score,
        val set: Int,
        val substitution: Event.Substitution,
        val team: Team,
    ) : AnalyzeError()

    data class WrongLibero(
        val matchReportId: MatchReportId,
        val score: Score,
        val set: Int,
        val libero: Event.Libero,
        val team: Team,
    ) : AnalyzeError()

    data class ActionStartNotFromService(
        val matchReportId: MatchReportId,
        val score: Score,
        val set: Int,
    ) : AnalyzeError()

    data class NoSetScoutData(
        val matchReportId: MatchReportId,
        val set: Int,
    ) : AnalyzeError()

    data class NoScoreForSetData(
        val matchReportId: MatchReportId,
        val set: Int,
    ) : AnalyzeError()
}

interface AnalyzeErrorReporter {

    fun report(analyzeError: AnalyzeError)
}

@Inject
class PrintingAnalyzeErrorReporter : AnalyzeErrorReporter {

    override fun report(analyzeError: AnalyzeError) {
        Logger.e(message = analyzeError.toString(), tag = "AnalyzeError")
    }
}
