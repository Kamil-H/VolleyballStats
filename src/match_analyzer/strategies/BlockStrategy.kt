package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class BlockStrategy : PlayActionStrategy<PlayAction.Block> {

    override fun check(input: AnalysisInput): List<PlayAction.Block> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val attack = plays[index - 1]
            val beforeAttack = plays.getOrNull(plays.indexOf(attack) - 1)
            val setterId = if (beforeAttack?.skill == Skill.Set) beforeAttack.player else null

            PlayAction.Block(
                generalInfo = input.generalInfo(play),
                attackerId = attack.player,
                setterId = setterId,
                afterSideOut = sideOutPlays.contains(attack),
            )
        }
}