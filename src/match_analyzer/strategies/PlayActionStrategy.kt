package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.models.PlayAction
import com.kamilh.models.Skill

fun interface PlayActionStrategy<T : PlayAction> {

    fun check(input: AnalysisInput): List<T>
}

fun AnalysisInput.sideOutPlays(): List<AnalysisInput.Play> {
    val first = plays.first()
    return plays.drop(1).takeWhile { it.team != first.team }
}

data class CheckData(
    val play: AnalysisInput.Play,
    val index: Int,
    val plays: List<AnalysisInput.Play>,
    val sideOutPlays: List<AnalysisInput.Play>,
)

inline fun <reified T : PlayAction> checkInput(input: AnalysisInput, mapper: CheckData.() -> T?): List<T> {
    return input.plays.filter {
        when (it.skill) {
            Skill.Attack -> T::class == PlayAction.Attack::class
            Skill.Block -> T::class == PlayAction.Block::class
            Skill.Dig -> T::class == PlayAction.Dig::class
            Skill.Set -> T::class == PlayAction.Set::class
            Skill.Freeball -> T::class == PlayAction.Freeball::class
            Skill.Receive -> T::class == PlayAction.Receive::class
            Skill.Serve -> T::class == PlayAction.Serve::class
        }
    }.mapNotNull {
        mapper(
            CheckData(
                play = it,
                index = input.plays.indexOf(it),
                plays = input.plays,
                sideOutPlays = input.sideOutPlays(),
            )
        )
    }
}