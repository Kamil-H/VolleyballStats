package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class ReceiveStrategy : PlayActionStrategy<PlayAction.Receive> {

    override fun check(input: AnalysisInput): List<PlayAction.Receive> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val serve = plays.getOrNull(index - 1)
            if (serve?.skill != Skill.Serve) {
                return@checkInput null
            }
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