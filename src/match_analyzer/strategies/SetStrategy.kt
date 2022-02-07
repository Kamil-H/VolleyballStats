package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class SetStrategy : PlayActionStrategy<PlayAction.Set> {

    override fun check(input: AnalysisInput): List<PlayAction.Set> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val attack = plays.getOrNull(index + 1)?.takeIf { it.skill == Skill.Attack }
            PlayAction.Set(
                generalInfo = input.generalInfo(play),
                attackerId = attack?.player,
                attackerPosition = attack?.position,
                sideOut = sideOutPlays.contains(play),
            )
        }
}