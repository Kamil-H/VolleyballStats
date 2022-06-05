package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.match_analyzer.AnalysisInput
import com.kamilh.volleyballstats.match_analyzer.generalInfo
import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.domain.models.Skill
import me.tatarka.inject.annotations.Inject

@Inject
class ServeStrategy : PlayActionStrategy<PlayAction.Serve> {

    override fun check(input: AnalysisInput): List<PlayAction.Serve> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val receiver = plays.getOrNull(index + 1)?.takeIf { it.skill == Skill.Receive }
            PlayAction.Serve(
                generalInfo = input.generalInfo(play),
                receiverId = receiver?.player,
                receiveEffect = receiver?.effect,
            )
        }
}