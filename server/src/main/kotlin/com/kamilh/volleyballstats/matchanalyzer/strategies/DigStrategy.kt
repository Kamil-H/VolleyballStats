package com.kamilh.volleyballstats.matchanalyzer.strategies

import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.extensions.divideExcluding
import com.kamilh.volleyballstats.matchanalyzer.AnalysisInput
import com.kamilh.volleyballstats.matchanalyzer.generalInfo
import me.tatarka.inject.annotations.Inject

@Inject
class DigStrategy : PlayActionStrategy<PlayAction.Dig> {

    override fun check(input: AnalysisInput): List<PlayAction.Dig> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val before = input.plays.divideExcluding(index).before.reversed()
            val attack = before.firstOrNull { it.skill == Skill.Attack && it.team != play.team }
            val beforeDig = plays.getOrNull(index - 1)
            val rebounderId = if (beforeDig?.skill == Skill.Block) beforeDig.player else null

            PlayAction.Dig(
                generalInfo = input.generalInfo(play),
                attackerId = attack?.player,
                rebounderId = rebounderId,
                afterSideOut = sideOutPlays.contains(attack),
            )
        }
}
