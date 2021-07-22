package com.kamilh.match_analyzer

import com.kamilh.models.MatchReport
import com.kamilh.models.PlayAction

data class MatchStatistics(
    val playActions: List<PlayAction> = emptyList()
)

class MatchReportAnalyzer(

) {

    fun analyze(matchReport: MatchReport): MatchStatistics {
        val matchId = matchReport.matchId

        if (matchReport.scout.sets.size != matchReport.scoutData.size) {
            throw IllegalStateException() // TODO: Change to some return
        }

        val sets = matchReport.scout.sets
        val scoutData = matchReport.scoutData


        return MatchStatistics()
    }
}