package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class DigStrategy : PlayActionStrategy<PlayAction.Dig> {

    override fun check(input: AnalysisInput): List<PlayAction.Dig> {
        val plays = input.plays
        val sideOutPlays = input.sideOutPlays()
        return plays
            .filter { it.skill == Skill.Dig }
            .map { play ->
                val index = plays.indexOf(play)
                val before = input.plays.divideExcluding(index).before.reversed()
                val attack = before.firstOrNull { it.skill == Skill.Attack && it.team != play.team }
                if (attack?.skill != Skill.Attack) {
                    error("No Attack before Dig")
                }
                val beforeDig = plays.getOrNull(index - 1)
                val rebounderId = if (beforeDig?.skill == Skill.Block) beforeDig.player else null

                PlayAction.Dig(
                    generalInfo = input.generalInfo(play),
                    attackerId = attack.player,
                    rebounderId = rebounderId,
                    afterSideOut = sideOutPlays.contains(attack),
                )
            }
    }
}