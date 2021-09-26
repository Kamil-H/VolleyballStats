package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class ReceiveStrategy : PlayActionStrategy<PlayAction.Receive> {

    override fun check(input: AnalysisInput): List<PlayAction.Receive> {
        val plays = input.plays
        return plays
            .filter { it.skill == Skill.Receive }
            .map { play ->
                val index = plays.indexOf(play)
                val serve = plays.getOrNull(index - 1)
                if (serve?.skill != Skill.Serve) {
                    error("No Serve before Receive")
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
}