package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction

class FreeballStrategy : PlayActionStrategy<PlayAction.Freeball> {

    override fun check(input: AnalysisInput): List<PlayAction.Freeball> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val before = plays.divideExcluding(index).before.reversed().firstOrNull { it.team != play.team }
            PlayAction.Freeball(
                generalInfo = input.generalInfo(play),
                afterSideOut = sideOutPlays.contains(before),
            )
        }
}