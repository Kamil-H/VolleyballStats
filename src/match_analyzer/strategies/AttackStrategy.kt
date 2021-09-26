package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.Effect
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class AttackStrategy : PlayActionStrategy<PlayAction.Attack> {

    override fun check(input: AnalysisInput): List<PlayAction.Attack> {
        val plays = input.plays
        val sideOutPlays = input.sideOutPlays()
        return plays
            .filter { it.skill == Skill.Attack }
            .map { play ->
                val index = plays.indexOf(play)
                val (_, afterAttack) = plays.divideExcluding(index)

                val blockAttempt: Boolean = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Block)
                val digAttempt: Boolean = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Dig)

                val sideOut: Boolean = sideOutPlays.contains(play)
                val receive = sideOutPlays.firstOrNull { it.skill == Skill.Receive }
                val receiveEffect = receive?.effect?.takeIf { sideOut }
                val receiverId = receive?.player?.takeIf { sideOut }

                val playBeforeAttack = plays.getOrNull(index - 1)
                val isSetBeforeAttack = playBeforeAttack?.skill == Skill.Set
                val setEffect = playBeforeAttack?.effect.takeIf { isSetBeforeAttack }
                val setterId = playBeforeAttack?.player.takeIf { isSetBeforeAttack }

                PlayAction.Attack(
                    generalInfo = input.generalInfo(play),
                    sideOut = sideOut,
                    blockAttempt = blockAttempt,
                    digAttempt = digAttempt,
                    receiveEffect = receiveEffect,
                    receiverId = receiverId,
                    setEffect = setEffect,
                    setterId = setterId,
                )
            }
    }
}