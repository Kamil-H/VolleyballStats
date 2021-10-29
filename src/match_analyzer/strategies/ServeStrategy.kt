package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

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