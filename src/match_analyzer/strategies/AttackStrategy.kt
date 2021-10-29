package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.Effect
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

class AttackStrategy : PlayActionStrategy<PlayAction.Attack> {

    override fun check(input: AnalysisInput): List<PlayAction.Attack> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val (_, afterAttack) = plays.divideExcluding(index)
            val sideOut = sideOutPlays.contains(play)
            val receiver = sideOutPlays.firstOrNull { it.skill == Skill.Receive }?.takeIf { sideOut }
            val setter = plays.getOrNull(index - 1)?.takeIf { it.skill == Skill.Set }
            PlayAction.Attack(
                generalInfo = input.generalInfo(play),
                sideOut = sideOut,
                blockAttempt = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Block),
                digAttempt = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Dig),
                receiveEffect = receiver?.effect,
                receiverId = receiver?.player,
                setEffect = setter?.effect,
                setterId = setter?.player,
            )
        }
}