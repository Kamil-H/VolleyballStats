package com.kamilh.volleyballstats.matchanalyzer.strategies

import com.kamilh.volleyballstats.domain.models.PlayAction
import com.kamilh.volleyballstats.extensions.divideExcluding
import com.kamilh.volleyballstats.matchanalyzer.AnalysisInput
import com.kamilh.volleyballstats.matchanalyzer.generalInfo
import me.tatarka.inject.annotations.Inject

@Inject
class FreeballStrategy : PlayActionStrategy<PlayAction.Freeball> {

    override fun check(input: AnalysisInput): List<PlayAction.Freeball> =
        checkInput(input) {
            val index = plays.indexOf(play)
            val before = plays.divideExcluding(index).before.reversed().firstOrNull { it.team != play.team }
            PlayAction.Freeball(
                generalInfo = input.generalInfo(play),
                afterSideOut = sideOutPlays.contains(before),
            )
        }
}
