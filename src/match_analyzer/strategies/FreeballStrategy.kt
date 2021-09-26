package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class FreeballStrategy : PlayActionStrategy<PlayAction.Freeball> {

    override fun check(input: AnalysisInput): List<PlayAction.Freeball> {
        val plays = input.plays
        val sideOutPlays = input.sideOutPlays()
        return plays
            .filter { it.skill == Skill.Freeball }
            .map { play ->
                val index = plays.indexOf(play)
                val before = plays.divideExcluding(index).before.reversed().firstOrNull { it.team != play.team }
                PlayAction.Freeball(
                    generalInfo = input.generalInfo(play),
                    afterSideOut = sideOutPlays.contains(before),
                )
            }
    }
}