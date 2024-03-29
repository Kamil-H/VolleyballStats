package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.match_analyzer.AnalysisInput
import com.kamilh.volleyballstats.match_analyzer.generalInfo
import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.domain.models.Skill
import me.tatarka.inject.annotations.Inject

@Inject
class SetStrategy : PlayActionStrategy<PlayAction.Set> {

    override fun check(input: AnalysisInput): List<PlayAction.Set> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val attack = plays.getOrNull(index + 1)?.takeIf { it.skill == Skill.Attack }
            PlayAction.Set(
                generalInfo = input.generalInfo(play),
                attackerId = attack?.player,
                attackerPosition = attack?.position,
                attackEffect = attack?.effect,
                sideOut = sideOutPlays.contains(play),
            )
        }
}