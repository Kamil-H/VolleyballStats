package com.kamilh.match_analyzer.strategies

import com.kamilh.extensions.divideExcluding
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.generalInfo
import com.kamilh.models.Effect
import com.kamilh.models.PlayAction
import com.kamilh.models.PlayerId
import com.kamilh.models.Skill

class AttackStrategy : PlayActionStrategy<PlayAction.Attack> {

    override fun check(input: AnalysisInput): List<PlayAction.Attack> {
        val plays = input.plays
        return plays
            .filter { it.skill == Skill.Attack }
            .map { play ->
                val index = plays.indexOf(play)
                val (beforeAttack, afterAttack) = plays.divideExcluding(index)

                val serves = beforeAttack.filter { it.skill == Skill.Serve }
                val receives = beforeAttack.filter { it.skill == Skill.Receive }
                val sets = beforeAttack.filter { it.skill == Skill.Set }
                val attacks = beforeAttack.filter { it.skill == Skill.Attack }

                val sideOut: Boolean
                val blockAttempt: Boolean = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Block)
                val digAttempt: Boolean = play.effect == Effect.Perfect && afterAttack.map { it.skill }.contains(Skill.Dig)
                val receiveEffect: Effect?
                val receiverId: PlayerId?
                val setEffect: Effect?
                val setterId: PlayerId?

                if (attacks.isEmpty() && serves.size == 1 && sets.size <= 1 && receives.size <= 1 && play.team != serves[0].team) {
                    sideOut = true
                    receiveEffect = beforeAttack.firstOrNull { it.skill == Skill.Receive }?.effect
                    receiverId = beforeAttack.firstOrNull { it.skill == Skill.Receive }?.player
                } else {
                    sideOut = false
                    receiveEffect = null
                    receiverId = null
                }

                val playBeforeAttack = plays.getOrNull(index - 1)
                if (playBeforeAttack?.skill == Skill.Set) {
                    setEffect = playBeforeAttack.effect
                    setterId = playBeforeAttack.player
                } else {
                    setEffect = null
                    setterId = null
                }

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