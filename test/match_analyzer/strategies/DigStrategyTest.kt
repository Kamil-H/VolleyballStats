package com.kamilh.match_analyzer.strategies

import com.kamilh.models.PlayerId
import com.kamilh.models.Skill
import com.kamilh.models.TeamId
import com.kamilh.models.analysisInputOf
import org.junit.Test

class DigStrategyTest {

    private val strategy = DigStrategy()
    private val firstTeam = TeamId(0)
    private val secondTeam = TeamId(1)
    private val firstPlayer = PlayerId(0)
    private val secondPlayer = PlayerId(1)

    @Test
    fun `test that attackerId is equal to the PlayerId that belongs to the player that attacked`() {
        // GIVEN
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam, secondPlayer),
            StrategyTestInput(Skill.Attack, firstTeam, firstPlayer),
            StrategyTestInput(Skill.Dig, secondTeam, secondPlayer),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().attackerId == firstPlayer)
    }

    @Test
    fun `test that rebounderId is equal to the PlayerId that belongs to the player that blocked`() {
        // GIVEN
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam, firstPlayer),
            StrategyTestInput(Skill.Set, secondTeam, firstPlayer),
            StrategyTestInput(Skill.Attack, secondTeam, firstPlayer),
            StrategyTestInput(Skill.Block, firstTeam, firstPlayer),
            StrategyTestInput(Skill.Dig, firstTeam, secondPlayer),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().rebounderId == firstPlayer)
    }

    @Test
    fun `test that afterSideOut is true when attack happened in side out action`() {
        // GIVEN
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, secondTeam),
            StrategyTestInput(Skill.Attack, secondTeam),
            StrategyTestInput(Skill.Dig, firstTeam),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().afterSideOut)
    }

    @Test
    fun `test that afterSideOut is false when attack happened not in side out action`() {
        // GIVEN
        val skills = listOf(
            StrategyTestInput(Skill.Serve, firstTeam),
            StrategyTestInput(Skill.Receive, secondTeam),
            StrategyTestInput(Skill.Set, secondTeam),
            StrategyTestInput(Skill.Attack, secondTeam),
            StrategyTestInput(Skill.Block, firstTeam),
            StrategyTestInput(Skill.Attack, firstTeam),
            StrategyTestInput(Skill.Dig, secondTeam),
        )

        val analysisInput = analysisInputOf(plays = skills.map { it.toPlay() })

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(!actions.first().afterSideOut)
    }
}