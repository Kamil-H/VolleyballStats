package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.models.PlayAction

fun interface PlayActionStrategy<T : PlayAction> {

    fun check(input: AnalysisInput): List<T>
}