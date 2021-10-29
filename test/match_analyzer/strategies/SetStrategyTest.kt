package com.kamilh.match_analyzer.strategies

import com.kamilh.models.*
import org.junit.Test

class SetStrategyTest {

    private val strategy = SetStrategy()

    @Test
    fun `test that attackerId is equal to the PlayerId that belongs to the player that attacked`() {
        // GIVEN
        val firstPlayer = PlayerId(0)
        val secondPlayer = PlayerId(1)
        val skills = listOf(
            Skill.Serve to secondPlayer,
            Skill.Set to firstPlayer,
            Skill.Attack to secondPlayer,
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
        assert(actions.first().attackerId == secondPlayer)
    }

    @Test
    fun `test that attackerPosition is equal to the PlayerPosition that belongs to the player that attacked`() {
        // GIVEN
        val firstPosition = PlayerPosition.P1
        val secondPosition = PlayerPosition.P2
        val skills = listOf(
            Skill.Serve to secondPosition,
            Skill.Set to firstPosition,
            Skill.Attack to secondPosition,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    position = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().attackerPosition == secondPosition)
    }

    @Test
    fun `test that sideOut is true when set happened in side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Block to firstTeam,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().sideOut)
    }

    @Test
    fun `test that sideOut is false when set happened not in side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Dig to firstTeam,
            Skill.Set to firstTeam,
            Skill.Attack to firstTeam,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(!actions.last().sideOut)
    }
}