package com.kamilh.volleyballstats.matchanalyzer.strategies

import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.matchanalyzer.AnalysisInput
import com.kamilh.volleyballstats.matchanalyzer.generalInfo
import me.tatarka.inject.annotations.Inject

@Inject
class ReceiveStrategy : PlayActionStrategy<PlayAction.Receive> {

    override fun check(input: AnalysisInput): List<PlayAction.Receive> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val serve = plays.getOrNull(index - 1)
            if (serve?.skill != Skill.Serve) {
                null
            } else {
                val afterReceive = plays.getOrNull(index + 1)
                val nextAfterReceive = plays.getOrNull(index + 2)
                PlayAction.Receive(
                    generalInfo = input.generalInfo(play),
                    serverId = serve.player,
                    attackEffect = nextAfterReceive?.effect?.takeIf { nextAfterReceive.skill == Skill.Attack && nextAfterReceive.team != serve.team },
                    setEffect = afterReceive?.effect?.takeIf { afterReceive.skill == Skill.Set && afterReceive.team != serve.team },
                )
            }
        }
}
