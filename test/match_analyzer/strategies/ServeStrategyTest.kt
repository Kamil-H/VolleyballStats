package com.kamilh.match_analyzer.strategies

import com.kamilh.models.*
import org.junit.Test

class ServeStrategyTest {

    private val strategy = ServeStrategy()
    private val firstTeam = TeamId(0)
    private val secondTeam = TeamId(1)

    @Test
    fun `test that receiverId is equal to the PlayerId that belongs to the player that received`() {
        // GIVEN
        val firstPlayer = PlayerId(0)
        val secondPlayer = PlayerId(1)
        val skills = listOf(
            Skill.Serve to firstPlayer,
            Skill.Receive to secondPlayer,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    player = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiverId == secondPlayer)
    }

    @Test
    fun `test that receiveEffect is equal to the PlayerId that belongs to the player that received`() {
        // GIVEN
        val receiveEffect = Effect.Positive
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam, effect = receiveEffect),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiveEffect == receiveEffect)
    }
}