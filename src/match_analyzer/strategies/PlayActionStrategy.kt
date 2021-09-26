package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

fun interface PlayActionStrategy<T : PlayAction> {

    fun check(input: AnalysisInput): List<T>
}

fun AnalysisInput.sideOutPlays(): List<AnalysisInput.Play> {
    val first = plays.firstOrNull()
    if (first?.skill != Skill.Serve) {
        error("plays are not starting from Serve")
    }
    return plays.drop(1).takeWhile { it.team != first.team }
}