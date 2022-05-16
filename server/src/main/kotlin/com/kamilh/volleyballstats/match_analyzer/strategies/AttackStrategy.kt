package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.extensions.divideExcluding
import com.kamilh.volleyballstats.match_analyzer.AnalysisInput
import com.kamilh.volleyballstats.match_analyzer.generalInfo
import com.kamilh.volleyballstats.models.Effect
import com.kamilh.volleyballstats.models.PlayAction
import com.kamilh.volleyballstats.models.Skill
import me.tatarka.inject.annotations.Inject

@Inject
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