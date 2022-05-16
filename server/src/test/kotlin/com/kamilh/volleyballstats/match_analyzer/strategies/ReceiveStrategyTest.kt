package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.models.*
import org.junit.Test

class ReceiveStrategyTest {

    private val strategy = ReceiveStrategy()
    private val firstTeam = TeamId(0)
    private val secondTeam = TeamId(1)

    @Test
    fun `test that actions are empty when there is no Serve before Receive`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Set, Skill.Receive)
        val analysisInput = analysisInputOf(plays = skills.map { analysisInputPlayOf(skill = it) })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.isEmpty())
    }

    @Test
    fun `test that serverId is equal to the PlayerId that belongs to the player that set`() {
        // GIVEN
        val firstPlayer = PlayerId(0)
        val secondPlayer = PlayerId(1)
        val skills = listOf(
            Skill.Serve to secondPlayer,
            Skill.Receive to firstPlayer,
            Skill.Set to secondPlayer,
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
        assert(actions.first().serverId == secondPlayer)
    }

    @Test
    fun `test that attackEffect is equal to the Effect that belongs to the player that attacked`() {
        // GIVEN
        val attackEffect = Effect.Positive
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, secondTeam),
            StrategyTestInput(Skill.Attack, secondTeam, effect = attackEffect),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().attackEffect == attackEffect)
    }

    @Test
    fun `test that setEffect is equal to the Effect that belongs to the player that set`() {
        // GIVEN
        val setEffect = Effect.Positive
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, secondTeam, effect = setEffect),
            StrategyTestInput(Skill.Attack, secondTeam),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().setEffect == setEffect)
    }

    @Test
    fun `test that attackEffect is equal not to the Effect that belongs to the player that attacked when he is from the other team`() {
        // GIVEN
        val attackEffect = Effect.Positive
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, firstTeam),
            StrategyTestInput(Skill.Attack, firstTeam, effect = attackEffect),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().attackEffect != attackEffect)
    }

    @Test
    fun `test that setEffect is not equal to the Effect that belongs to the player that set when he is from the other team`() {
        // GIVEN
        val setEffect = Effect.Positive
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, firstTeam, effect = setEffect),
            StrategyTestInput(Skill.Attack, firstTeam),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().setEffect != setEffect)
    }
}